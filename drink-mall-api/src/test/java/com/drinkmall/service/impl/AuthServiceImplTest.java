package com.drinkmall.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.LoginRequest;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.PhaseOneCoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private WxMaService wxMaService;
    @Mock private UserMapper userMapper;
    @Mock private PhaseOneCoreService phaseOneCoreService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(wxMaService, userMapper, phaseOneCoreService);
    }

    @Test
    void userAgreementMustBeCheckedBeforeWechatLogin() {
        LoginRequest request = loginRequest();
        request.setUserAgreement(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(wxMaService, userMapper, phaseOneCoreService);
    }

    @Test
    void privacyPolicyMustBeCheckedBeforeWechatLogin() {
        LoginRequest request = loginRequest();
        request.setPrivacyPolicy(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(wxMaService, userMapper, phaseOneCoreService);
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setCode("wx-code");
        request.setUserAgreement(true);
        request.setPrivacyPolicy(true);
        return request;
    }
}
