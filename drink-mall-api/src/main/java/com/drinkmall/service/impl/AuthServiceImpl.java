package com.drinkmall.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.LoginRequest;
import com.drinkmall.dto.LoginResponse;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.AuthService;
import com.drinkmall.service.PhaseOneCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final WxMaService wxMaService;
    private final UserMapper userMapper;
    private final PhaseOneCoreService phaseOneCoreService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (!Boolean.TRUE.equals(request.getUserAgreement())) {
            throw new BusinessException(400, "请先同意用户协议");
        }
        if (!Boolean.TRUE.equals(request.getPrivacyPolicy())) {
            throw new BusinessException(400, "请先同意隐私政策");
        }

        try {
            WxMaJscode2SessionResult session = wxMaService.jsCode2SessionInfo(request.getCode());
            String openid = session.getOpenid();

            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
            );

            boolean isNewUser = false;
            if (user == null) {
                user = phaseOneCoreService.registerUser(openid, session.getUnionid(), request.getInviterId(), request.getInviteCode(), request.resolveRegisterSource(), false);
                isNewUser = true;
                log.info("Created new user with openid: {}", openid);
            }

            StpUtil.login(user.getId());

            return LoginResponse.builder()
                    .userId(user.getId())
                    .token(StpUtil.getTokenValue())
                    .nickname(user.getNickname())
                    .avatarUrl(user.getAvatarUrl())
                    .ageVerified(user.getAgeVerified())
                    .isNewUser(isNewUser)
                    .build();

        } catch (WxErrorException e) {
            log.error("WeChat login failed: {}", e.getMessage());
            throw new BusinessException(500, "微信登录失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginResponse demoLogin() {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, "demo-openid")
        );
        if (user == null) {
            user = new User();
            user.setOpenid("demo-openid");
            user.setUnionid("demo-unionid");
            user.setNickname("演示用户");
            user.setAvatarUrl("/static/images/page-profile.png");
            user.setPhone("13800138000");
            user.setBalance(new BigDecimal("9999.00"));
            user.setFrozenBalance(BigDecimal.ZERO);
            user.setPoints(1888);
            user.setDistributionLevel("normal");
            user.setTeamPerformance(BigDecimal.ZERO);
            user.setDfBalance(new BigDecimal("1500.00"));
            user.setWineBeanBalance(BigDecimal.ZERO);
            user.setOptionBalance(BigDecimal.ZERO);
            user.setInviteCode("DEMO1001");
            user.setRegisterInviteCode(null);
            user.setRegisterSource("demo");
            user.setSeedAccount(true);
            user.setRealNameStatus("approved");
            user.setMainZonePaidAmount(BigDecimal.ZERO);
            user.setAgeVerified(true);
            user.setAgeVerifiedAt(LocalDateTime.now());
            user.setStatus(1);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        }

        StpUtil.login(user.getId());
        return LoginResponse.builder()
                .userId(user.getId())
                .token(StpUtil.getTokenValue())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .ageVerified(user.getAgeVerified())
                .isNewUser(false)
                .build();
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public boolean isLoggedIn() {
        return StpUtil.isLogin();
    }
}
