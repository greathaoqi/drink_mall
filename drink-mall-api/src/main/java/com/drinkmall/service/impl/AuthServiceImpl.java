package com.drinkmall.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.LoginRequest;
import com.drinkmall.dto.LoginResponse;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMaService;
import me.chanjar.weixin.mp.bean.WxMaJscode2SessionResult;
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
                user = new User();
                user.setOpenid(openid);
                user.setBalance(BigDecimal.ZERO);
                user.setPoints(0);
                user.setAgeVerified(false);
                user.setStatus(1);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.insert(user);
                isNewUser = true;
                log.info("Created new user with openid: {}", openid);
            }

            StpUtil.login(user.getId());

            return LoginResponse.builder()
                    .userId(user.getId())
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
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public boolean isLoggedIn() {
        return StpUtil.isLogin();
    }
}
