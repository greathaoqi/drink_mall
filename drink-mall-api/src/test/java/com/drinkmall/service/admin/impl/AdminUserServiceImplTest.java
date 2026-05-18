package com.drinkmall.service.admin.impl;

import com.drinkmall.dto.AdminReferralNodeResponse;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.OrderMapper;
import com.drinkmall.mapper.PointsLogMapper;
import com.drinkmall.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock private UserMapper userMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private PointsLogMapper pointsLogMapper;
    @Mock private OperationLogMapper operationLogMapper;

    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setUp() {
        adminUserService = new AdminUserServiceImpl(userMapper, orderMapper, pointsLogMapper, operationLogMapper);
    }

    @Test
    void deepReferralAuditReturnsDepthLimitedNodesAndWritesOperationLog() {
        User direct = user(2L, 1L, "direct");
        User indirect = user(3L, 2L, "indirect");
        when(userMapper.selectReferralDescendants(1L, 3)).thenReturn(List.of(direct, indirect));

        List<AdminReferralNodeResponse> nodes = adminUserService.getReferralAuditTree(99L, 1L, 3);

        assertThat(nodes).extracting(AdminReferralNodeResponse::getDepth).containsExactly(1, 2);
        assertThat(nodes).extracting(AdminReferralNodeResponse::getUserId).containsExactly(2L, 3L);

        ArgumentCaptor<OperationLog> log = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(log.capture());
        assertThat(log.getValue().getAdminUserId()).isEqualTo(99L);
        assertThat(log.getValue().getModule()).isEqualTo("referral");
        assertThat(log.getValue().getAction()).isEqualTo("deep_query");
        assertThat(log.getValue().getTargetId()).isEqualTo("1");
        assertThat(log.getValue().getDetail()).contains("maxDepth=3", "resultCount=2");
    }

    private User user(Long id, Long inviterId, String nickname) {
        User user = new User();
        user.setId(id);
        user.setInviterId(inviterId);
        user.setNickname(nickname);
        user.setDistributionLevel("normal");
        user.setReferralDepth(id == 2L ? 1 : 2);
        return user;
    }
}
