package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.DistributionLevelItemResponse;
import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.dto.MemberCenterResponse;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.dto.WithdrawalRequest;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Withdrawal;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BalanceLogMapper balanceLogMapper;
    private final PointsLogMapper pointsLogMapper;
    private final WithdrawalMapper withdrawalMapper;
    private final OrderMapper orderMapper;

    private static final BigDecimal COUNTY_TARGET = new BigDecimal("50000.00");
    private static final BigDecimal CITY_TARGET = new BigDecimal("150000.00");
    private static final String COUNTY_LEVEL = "county";
    private static final String CITY_LEVEL = "city";
    private static final Map<String, String> LEVEL_NAMES = Map.of(
            COUNTY_LEVEL, "县级联营商",
            CITY_LEVEL, "市级联营商"
    );

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User findByOpenid(String openid) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );
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
        return UserInfoResponse.fromEntity(user);
    }

    @Override
    public MemberCenterResponse getMemberCenter(Long userId) {
        User user = requireUser(userId);
        BigDecimal withdrawableBalance = money(user.getBalance());
        BigDecimal frozenBalance = money(user.getFrozenBalance());
        BigDecimal dfBalance = money(user.getDfBalance());
        BigDecimal teamPerformance = money(user.getTeamPerformance());
        Integer points = user.getPoints() == null ? 0 : user.getPoints();
        String levelCode = normalizeLevel(user.getDistributionLevel(), teamPerformance);

        return MemberCenterResponse.builder()
                .profile(MemberCenterResponse.Profile.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .avatarUrl(user.getAvatarUrl())
                        .phone(user.getPhone())
                        .maskedPhone(maskPhone(user.getPhone()))
                        .memberLevelCode(levelCode)
                        .memberLevelName(levelName(levelCode))
                        .memberTitle(levelName(levelCode) + " · 生态合伙人")
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
        String currentCode = normalizeLevel(user.getDistributionLevel(), performance);
        BigDecimal nextTarget = CITY_LEVEL.equals(currentCode) ? CITY_TARGET : CITY_TARGET;
        BigDecimal upgradeDifference = nextTarget.subtract(performance).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal progressPercent = nextTarget.compareTo(BigDecimal.ZERO) == 0
                ? new BigDecimal("100.00")
                : performance.multiply(new BigDecimal("100")).divide(nextTarget, 2, RoundingMode.HALF_UP)
                    .min(new BigDecimal("100.00"));
        List<DistributionLevelItemResponse> levels = List.of(
                levelItem(COUNTY_LEVEL, "县级联营商", COUNTY_TARGET, performance),
                levelItem(CITY_LEVEL, "市级联营商", CITY_TARGET, performance)
        );

        DistributionLevelItemResponse currentLevel = levels.stream()
                .filter(level -> level.getCode().equals(currentCode))
                .findFirst()
                .orElse(levels.get(0));

        return DistributionLevelOverviewResponse.builder()
                .currentLevel(currentLevel)
                .performanceAmount(performance)
                .nextTargetAmount(nextTarget)
                .upgradeDifference(upgradeDifference)
                .progressPercent(progressPercent)
                .levels(levels)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setNickname(nickname);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void applyWithdrawal(Long userId, WithdrawalRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(404, "用户不存在");
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(request.getAmount()) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        long pending = withdrawalMapper.selectCount(
            new LambdaQueryWrapper<Withdrawal>()
                .eq(Withdrawal::getUserId, userId)
                .eq(Withdrawal::getStatus, "pending")
        );
        if (pending > 0) throw new BusinessException(400, "您有待审核的提现申请，请等待处理后再提交");

        user.setBalance(balance.subtract(request.getAmount()));
        BigDecimal frozen = user.getFrozenBalance() != null ? user.getFrozenBalance() : BigDecimal.ZERO;
        user.setFrozenBalance(frozen.add(request.getAmount()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setAmount(request.getAmount());
        withdrawal.setBankName(request.getBankName());
        withdrawal.setBankAccount(request.getBankAccount());
        withdrawal.setAccountName(request.getAccountName());
        withdrawal.setStatus("pending");
        withdrawal.setCreatedAt(LocalDateTime.now());
        withdrawalMapper.insert(withdrawal);

        BalanceLog balanceLog = new BalanceLog();
        balanceLog.setUserId(userId);
        balanceLog.setChangeType("withdrawal");
        balanceLog.setAmount(request.getAmount().negate());
        balanceLog.setBeforeBalance(balance);
        balanceLog.setAfterBalance(balance.subtract(request.getAmount()));
        balanceLog.setRemark("申请提现");
        balanceLog.setCreatedAt(LocalDateTime.now());
        balanceLogMapper.insert(balanceLog);
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

    private String normalizeLevel(String levelCode, BigDecimal performance) {
        if (CITY_LEVEL.equals(levelCode) || COUNTY_LEVEL.equals(levelCode)) {
            return levelCode;
        }
        if (performance.compareTo(CITY_TARGET) >= 0) {
            return CITY_LEVEL;
        }
        return COUNTY_LEVEL;
    }

    private String levelName(String levelCode) {
        return LEVEL_NAMES.getOrDefault(levelCode, "县级联营商");
    }

    private DistributionLevelItemResponse levelItem(String code, String name, BigDecimal entryAmount, BigDecimal performance) {
        return DistributionLevelItemResponse.builder()
                .code(code)
                .name(name)
                .entryAmount(entryAmount)
                .upgradeDifference(entryAmount.subtract(performance).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP))
                .achieved(performance.compareTo(entryAmount) >= 0)
                .benefits(List.of(
                        "直推首购 20% 佣金",
                        "招商奖励 20%",
                        "扶商奖励 10%",
                        "自购 5 折优惠",
                        "可提交体验店申请"
                ))
                .build();
    }
}
