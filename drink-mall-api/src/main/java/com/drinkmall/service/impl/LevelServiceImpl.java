package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.Order;
import com.drinkmall.entity.OrderItem;
import com.drinkmall.entity.Product;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.enums.ProductZoneType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private static final String PENDING_CONFIRMATION = "待业务确认";

    private final UserMapper userMapper;
    private final SysConfigMapper sysConfigMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    @Transactional
    public void recordMainProductPaid(Long userId, BigDecimal amount, String orderNo) {
        User user = requireUser(userId);
        BigDecimal before = value(user.getMainZonePaidAmount());
        BigDecimal after = before.add(amount);
        user.setMainZonePaidAmount(after);
        if (after.compareTo(configDecimal("level.promoter.main_paid_threshold")) >= 0
                && levelOrder(user.getDistributionLevel()) < levelOrder(UserLevel.PROMOTER.getCode())) {
            user.setDistributionLevel(UserLevel.PROMOTER.getCode());
            log(null, userId, "upgrade_promoter", before + "->" + after + ", orderNo=" + orderNo);
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void upgradeByInvestmentOrder(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null && ProductZoneType.INVESTMENT.getCode().equals(product.getZoneType())
                    && product.getInvestmentLevelCode() != null && !product.getInvestmentLevelCode().isBlank()) {
                upgradeTo(order.getUserId(), product.getInvestmentLevelCode(), "investment_order", order.getId());
            }
        }
    }

    @Override
    @Transactional
    public void upgradeTo(Long userId, String targetLevel, String reason, Long businessId) {
        User user = requireUser(userId);
        if (!hasLevelOrder(targetLevel)) {
            throw new BusinessException(400, "无效等级: " + targetLevel);
        }
        if (levelOrder(targetLevel) <= levelOrder(user.getDistributionLevel())) {
            return;
        }
        String before = user.getDistributionLevel();
        user.setDistributionLevel(targetLevel);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        log(null, userId, "upgrade_level", before + "->" + targetLevel + ", reason=" + reason + ", businessId=" + businessId);
    }

    private User requireUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private int levelOrder(String level) {
        return configInteger("level." + level + ".order");
    }

    private BigDecimal configDecimal(String key) {
        return new BigDecimal(requiredConfigValue(key));
    }

    private Integer configInteger(String key) {
        return new BigDecimal(requiredConfigValue(key)).intValue();
    }

    private String requiredConfigValue(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        if (isPending(config.getConfigValue())) {
            throw new BusinessException(500, "系统配置待业务确认: " + key);
        }
        return config.getConfigValue().trim();
    }

    private boolean hasLevelOrder(String level) {
        if (level == null || level.isBlank()) {
            return false;
        }
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, "level." + level + ".order"));
        return config != null && config.getConfigValue() != null && !config.getConfigValue().isBlank() && !isPending(config.getConfigValue());
    }

    private boolean isPending(String value) {
        return value != null && PENDING_CONFIRMATION.equals(value.trim());
    }

    private BigDecimal value(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private void log(Long adminId, Long userId, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule("level");
        log.setAction(action);
        log.setTargetId(String.valueOf(userId));
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
