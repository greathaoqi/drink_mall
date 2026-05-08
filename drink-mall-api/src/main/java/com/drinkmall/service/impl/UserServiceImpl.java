package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

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
}
