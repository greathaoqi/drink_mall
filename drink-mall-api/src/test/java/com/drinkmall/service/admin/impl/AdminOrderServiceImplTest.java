package com.drinkmall.service.admin.impl;

import com.drinkmall.entity.Aftersale;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.mapper.AddressMapper;
import com.drinkmall.mapper.AftersaleMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.OrderItemMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.ProductMapper;
import com.drinkmall.service.LevelService;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceImplTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private AftersaleMapper aftersaleMapper;
    @Mock private ProductMapper productMapper;
    @Mock private AddressMapper addressMapper;
    @Mock private PhaseOneCoreService phaseOneCoreService;
    @Mock private OperationLogMapper operationLogMapper;
    @Mock private LevelService levelService;
    @Mock private RewardService rewardService;

    private AdminOrderServiceImpl adminOrderService;

    @BeforeEach
    void setUp() {
        adminOrderService = new AdminOrderServiceImpl(
                orderMapper,
                orderItemMapper,
                aftersaleMapper,
                productMapper,
                addressMapper,
                phaseOneCoreService,
                operationLogMapper,
                levelService,
                rewardService
        );
    }

    @Test
    void closeAftersaleMarksClosedAndWritesOperationLog() {
        Aftersale aftersale = aftersale();
        when(aftersaleMapper.selectById(10L)).thenReturn(aftersale);

        adminOrderService.closeAftersale(10L, "user cancelled");

        assertThat(aftersale.getStatus()).isEqualTo("closed");
        assertThat(aftersale.getAdminRemark()).isEqualTo("user cancelled");
        assertThat(aftersale.getProcessedAt()).isNotNull();
        verify(aftersaleMapper).updateById(aftersale);
        assertOperationLogged("aftersale", "close", "10");
    }

    @Test
    void completeAftersaleMarksCompletedAndWritesOperationLog() {
        Aftersale aftersale = aftersale();
        when(aftersaleMapper.selectById(10L)).thenReturn(aftersale);

        adminOrderService.completeAftersale(10L, "refund finished");

        assertThat(aftersale.getStatus()).isEqualTo("completed");
        assertThat(aftersale.getAdminRemark()).isEqualTo("refund finished");
        assertThat(aftersale.getProcessedAt()).isNotNull();
        verify(aftersaleMapper).updateById(aftersale);
        assertOperationLogged("aftersale", "complete", "10");
    }

    @Test
    void recordOfflineAftersaleResultStoresResultAndWritesOperationLog() {
        Aftersale aftersale = aftersale();
        when(aftersaleMapper.selectById(10L)).thenReturn(aftersale);

        adminOrderService.recordOfflineAftersaleResult(10L, "offline replacement shipped");

        assertThat(aftersale.getStatus()).isEqualTo("offline_processed");
        assertThat(aftersale.getOfflineProcessResult()).isEqualTo("offline replacement shipped");
        assertThat(aftersale.getOfflineProcessedAt()).isNotNull();
        verify(aftersaleMapper).updateById(aftersale);
        assertOperationLogged("aftersale", "offline_process", "10");
    }

    private Aftersale aftersale() {
        Aftersale aftersale = new Aftersale();
        aftersale.setId(10L);
        aftersale.setOrderId(20L);
        aftersale.setUserId(30L);
        aftersale.setStatus("pending");
        return aftersale;
    }

    private void assertOperationLogged(String module, String action, String targetId) {
        ArgumentCaptor<OperationLog> log = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(log.capture());
        assertThat(log.getValue().getModule()).isEqualTo(module);
        assertThat(log.getValue().getAction()).isEqualTo(action);
        assertThat(log.getValue().getTargetId()).isEqualTo(targetId);
        assertThat(log.getValue().getCreatedAt()).isNotNull();
    }
}
