package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.DistributionLevelItemResponse;
import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.dto.InviterResponse;
import com.drinkmall.dto.MemberCenterResponse;
import com.drinkmall.dto.TeamMemberResponse;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.UserService;
import com.drinkmall.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String PENDING_CONFIRMATION = "待业务确认";
    private static final List<String> DISTRIBUTION_LEVEL_CODES = List.of(
            UserLevel.COUNTY.getCode(),
            UserLevel.CITY.getCode(),
            UserLevel.PROVINCE.getCode()
    );

    private final UserMapper userMapper;
    private final BalanceLogMapper balanceLogMapper;
    private final PointsLogMapper pointsLogMapper;
    private final WithdrawalMapper withdrawalMapper;
    private final OrderMapper orderMapper;
    private final SysConfigMapper sysConfigMapper;
    private final WithdrawalService withdrawalService;

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User findByOpenid(String openid) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
    }

    @Override
    @Transactional
    public void verifyAge(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setAgeVerified(true);
        user.setAgeVerifiedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("User {} verified age at {}", userId, user.getAgeVerifiedAt());
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        UserInfoResponse response = UserInfoResponse.fromEntity(user);
        if (user.getInviterId() != null) {
            User inviter = userMapper.selectById(user.getInviterId());
            response.setInviterName(inviter == null ? null : inviter.getNickname());
        }
        response.setShowOption(optionVisible());
        return response;
    }

    @Override
    public MemberCenterResponse getMemberCenter(Long userId) {
        User user = requireUser(userId);
        BigDecimal withdrawableBalance = money(user.getBalance());
        BigDecimal frozenBalance = money(user.getFrozenBalance());
        BigDecimal dfBalance = money(user.getDfBalance());
        BigDecimal teamPerformance = money(user.getTeamPerformance());
        Integer points = user.getPoints() == null ? 0 : user.getPoints();
        String levelCode = normalizeLevel(user.getDistributionLevel());
        String levelName = levelName(levelCode);

        return MemberCenterResponse.builder()
                .profile(MemberCenterResponse.Profile.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .avatarUrl(user.getAvatarUrl())
                        .phone(user.getPhone())
                        .maskedPhone(maskPhone(user.getPhone()))
                        .memberLevelCode(levelCode)
                        .memberLevelName(levelName)
                        .memberTitle(levelName)
                        .ageVerified(user.getAgeVerified())
                        .build())
                .summary(MemberCenterResponse.Summary.builder()
                        .withdrawableBalance(withdrawableBalance)
                        .teamPerformance(teamPerformance)
                        .points(points)
                        .build())
                .assets(MemberCenterResponse.Assets.builder()
                        .withdrawableBalance(withdrawableBalance)
                        .frozenBalance(frozenBalance)
                        .dfBalance(dfBalance)
                        .points(points)
                        .build())
                .orderSummary(MemberCenterResponse.OrderSummary.builder()
                        .pendingPayment(countOrders(userId, "pending"))
                        .pendingShipment(countOrders(userId, "paid"))
                        .pendingReceipt(countOrders(userId, "shipped"))
                        .completed(countOrders(userId, "completed"))
                        .afterSale(countOrders(userId, "aftersale"))
                        .build())
                .build();
    }

    @Override
    public DistributionLevelOverviewResponse getDistributionLevelOverview(Long userId) {
        User user = requireUser(userId);
        BigDecimal performance = money(user.getTeamPerformance());
        String currentCode = normalizeLevel(user.getDistributionLevel());
        List<LevelRule> rules = distributionLevelRules();
        List<DistributionLevelItemResponse> levels = rules.stream()
                .map(rule -> levelItem(rule, performance))
                .toList();
        LevelRule currentRule = rules.stream()
                .filter(rule -> rule.code().equals(currentCode))
                .findFirst()
                .orElseGet(() -> rules.isEmpty() ? null : rules.get(0));
        LevelRule nextRule = rules.stream()
                .filter(rule -> currentRule == null || rule.order() > currentRule.order())
                .min(Comparator.comparingInt(LevelRule::order))
                .orElse(currentRule);
        BigDecimal nextTarget = nextRule == null ? BigDecimal.ZERO : nextRule.threshold();
        BigDecimal upgradeDifference = nextTarget.subtract(performance).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal progressPercent = nextTarget.compareTo(BigDecimal.ZERO) == 0
                ? new BigDecimal("100.00")
                : performance.multiply(new BigDecimal("100")).divide(nextTarget, 2, RoundingMode.HALF_UP)
                    .min(new BigDecimal("100.00"));

        return DistributionLevelOverviewResponse.builder()
                .currentLevel(currentRule == null ? null : levelItem(currentRule, performance))
                .performanceAmount(performance)
                .nextTargetAmount(nextTarget)
                .upgradeDifference(upgradeDifference)
                .progressPercent(progressPercent)
                .levels(levels)
                .build();
    }

    @Override
    public InviterResponse getInviter(Long userId) {
        User user = requireUser(userId);
        if (user.getInviterId() == null) {
            return null;
        }
        return InviterResponse.fromUser(userMapper.selectById(user.getInviterId()));
    }

    @Override
    public List<TeamMemberResponse> getDirectInvitees(Long userId) {
        requireUser(userId);
        return userMapper.selectDirectInvitees(userId).stream().map(TeamMemberResponse::fromUser).toList();
    }

    @Override
    public List<TeamMemberResponse> getIndirectInvitees(Long userId) {
        requireUser(userId);
        return userMapper.selectIndirectInvitees(userId).stream().map(TeamMemberResponse::fromUser).toList();
    }

    @Override
    public Long getTeamTotal(Long userId) {
        requireUser(userId);
        return userMapper.countTeamMembers(userId);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setNickname(nickname);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void applyWithdrawal(Long userId, WithdrawalRequest request) {
        withdrawalService.submit(userId, request);
    }

    @Override
    public Page<BalanceLog> getBalanceLogs(Long userId, Integer page, Integer size) {
        return balanceLogMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<BalanceLog>()
                        .eq(BalanceLog::getUserId, userId)
                        .orderByDesc(BalanceLog::getCreatedAt)
        );
    }

    @Override
    public Page<PointsLog> getPointsLogs(Long userId, Integer page, Integer size) {
        return pointsLogMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<PointsLog>()
                        .eq(PointsLog::getUserId, userId)
                        .orderByDesc(PointsLog::getCreatedAt)
        );
    }

    @Override
    public Page<Withdrawal> getUserWithdrawals(Long userId, Integer page, Integer size) {
        return withdrawalMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Withdrawal>()
                        .eq(Withdrawal::getUserId, userId)
                        .orderByDesc(Withdrawal::getCreatedAt)
        );
    }

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private Long countOrders(Long userId, String status) {
        return orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .eq(Order::getStatus, status)
        );
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String normalizeLevel(String levelCode) {
        return levelCode == null || levelCode.isBlank() ? UserLevel.NORMAL.getCode() : levelCode;
    }

    private List<LevelRule> distributionLevelRules() {
        List<LevelRule> levels = new ArrayList<>();
        for (String code : DISTRIBUTION_LEVEL_CODES) {
            String name = configValue("level." + code + ".name");
            if (name == null) {
                continue;
            }
            if (isPending(name)) {
                throw new BusinessException(500, "系统配置待业务确认: level." + code + ".name");
            }
            BigDecimal threshold = levelThreshold(code);
            int order = levelOrder(code);
            levels.add(new LevelRule(code, name.trim(), threshold, order, levelBenefits(code)));
        }
        levels.sort(Comparator.comparingInt(LevelRule::order));
        return levels;
    }

    private String levelName(String levelCode) {
        return requiredConfigValue("level." + levelCode + ".name");
    }

    private BigDecimal levelThreshold(String levelCode) {
        return new BigDecimal(requiredConfigValue("level." + levelCode + ".main_performance_threshold"));
    }

    private int levelOrder(String levelCode) {
        return Integer.parseInt(requiredConfigValue("level." + levelCode + ".order"));
    }

    private DistributionLevelItemResponse levelItem(LevelRule rule, BigDecimal performance) {
        return DistributionLevelItemResponse.builder()
                .code(rule.code())
                .name(rule.name())
                .entryAmount(rule.threshold())
                .upgradeDifference(rule.threshold().subtract(performance).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP))
                .achieved(performance.compareTo(rule.threshold()) >= 0)
                .benefits(rule.benefits())
                .build();
    }

    private List<String> levelBenefits(String levelCode) {
        String value = configValue("level." + levelCode + ".benefits");
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private String requiredConfigValue(String key) {
        String value = configValue(key);
        if (value == null || value.isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        if (isPending(value)) {
            throw new BusinessException(500, "系统配置待业务确认: " + key);
        }
        return value.trim();
    }

    private String configValue(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        return config == null ? null : config.getConfigValue();
    }

    private boolean isPending(String value) {
        return value != null && PENDING_CONFIRMATION.equals(value.trim());
    }

    private boolean optionVisible() {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, "asset.option.visible_in_mini"));
        return config != null && Boolean.parseBoolean(config.getConfigValue());
    }

    private record LevelRule(String code, String name, BigDecimal threshold, int order, List<String> benefits) {}
}
