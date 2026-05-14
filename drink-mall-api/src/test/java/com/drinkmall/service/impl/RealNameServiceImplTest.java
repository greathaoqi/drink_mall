package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.RealNameSubmitRequest;
import com.drinkmall.entity.RealNameVerification;
import com.drinkmall.entity.User;
import com.drinkmall.enums.RealNameStatus;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.RealNameVerificationMapper;
import com.drinkmall.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealNameServiceImplTest {

    @Mock
    private RealNameVerificationMapper realNameVerificationMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private OperationLogMapper operationLogMapper;

    private RealNameServiceImpl realNameService;

    @BeforeEach
    void setUp() {
        realNameService = new RealNameServiceImpl(realNameVerificationMapper, userMapper, operationLogMapper);
    }

    @Test
    void approvingDuplicateIdCardFails() {
        RealNameVerification verification = verification(1L, 10L, "110101199001011234");
        when(realNameVerificationMapper.selectById(1L)).thenReturn(verification);
        when(realNameVerificationMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> realNameService.review(1L, 99L, true, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("身份证");
    }

    @Test
    void rejectingRequiresReason() {
        RealNameVerification verification = verification(2L, 20L, "110101199001011235");
        when(realNameVerificationMapper.selectById(2L)).thenReturn(verification);

        assertThatThrownBy(() -> realNameService.review(2L, 99L, false, " "))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("驳回原因");
    }

    @Test
    void statusReturnsLatestSubmissionWhenExists() {
        RealNameVerification verification = verification(3L, 30L, "110101199001011236");
        verification.setStatus(RealNameStatus.PENDING.getCode());
        when(realNameVerificationMapper.selectOne(any())).thenReturn(verification);

        RealNameVerification status = realNameService.getStatus(30L);

        assertThat(status.getId()).isEqualTo(3L);
        assertThat(status.getStatus()).isEqualTo(RealNameStatus.PENDING.getCode());
    }

    @Test
    void submitUpdatesUserStatusToPending() {
        when(userMapper.selectById(40L)).thenReturn(user(40L));
        when(realNameVerificationMapper.selectCount(any())).thenReturn(0L);

        realNameService.submit(40L, submitRequest());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertThat(captor.getValue().getRealNameStatus()).isEqualTo(RealNameStatus.PENDING.getCode());
    }

    @Test
    void approvedUserCannotSubmitAgain() {
        User user = user(50L);
        user.setRealNameStatus(RealNameStatus.APPROVED.getCode());
        when(userMapper.selectById(50L)).thenReturn(user);

        assertThatThrownBy(() -> realNameService.submit(50L, submitRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已通过");
    }

    @Test
    void reviewedSubmissionCannotBeReviewedAgain() {
        RealNameVerification verification = verification(6L, 60L, "110101199001011238");
        verification.setStatus(RealNameStatus.APPROVED.getCode());
        when(realNameVerificationMapper.selectById(6L)).thenReturn(verification);

        assertThatThrownBy(() -> realNameService.review(6L, 99L, true, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已处理");
    }

    private RealNameVerification verification(Long id, Long userId, String idCardNo) {
        RealNameVerification verification = new RealNameVerification();
        verification.setId(id);
        verification.setUserId(userId);
        verification.setRealName("张三");
        verification.setIdCardNo(idCardNo);
        verification.setStatus(RealNameStatus.PENDING.getCode());
        return verification;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private RealNameSubmitRequest submitRequest() {
        RealNameSubmitRequest request = new RealNameSubmitRequest();
        request.setRealName("张三");
        request.setIdCardNo("110101199001011237");
        request.setFrontImageUrl("https://example.com/front.jpg");
        request.setBackImageUrl("https://example.com/back.jpg");
        return request;
    }
}
