package com.drinkmall.service;

import com.drinkmall.dto.UserInfoResponse;
import com.drinkmall.entity.User;

public interface UserService {
    User getById(Long userId);
    User findByOpenid(String openid);
    void verifyAge(Long userId);
    UserInfoResponse getUserInfo(Long userId);
}
