package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.drinkmall.entity.AssetLog;
import com.drinkmall.entity.BalanceLog;
import com.drinkmall.entity.PointsLog;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.BalanceLogMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.WithdrawalMapper;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.RewardService;
import com.drinkmall.service.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminFinanceServiceImplTest {

    @Mock private WithdrawalMapper withdrawalMapper;
    @Mock private PointsLogMapper pointsLogMapper;
    @Mock private BalanceLogMapper balanceLogMapper;
    @Mock private AssetService assetService;
    @Mock private RewardService rewardService;
    @Mock private WithdrawalService withdrawalService;

    private AdminFinanceServiceImpl adminFinanceService;

    @BeforeEach
    void setUp() {
        adminFinanceService = new AdminFinanceServiceImpl(
                withdrawalMapper,
                pointsLogMapper,
                balanceLogMapper,
                assetService,
                rewardService,
                withdrawalService
        );
    }

    @Test
    void assetLogsAllowGlobalQueryWithoutUserId() {
        adminFinanceService.getAssetLogs(null, AssetType.DF, 1, 20);

        verify(assetService).getLogs(null, AssetType.DF, 1, 20);
    }
}
