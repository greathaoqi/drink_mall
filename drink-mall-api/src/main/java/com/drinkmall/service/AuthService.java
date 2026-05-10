package com.drinkmall.service;

import com.drinkmall.dto.LoginRequest;
import com.drinkmall.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse demoLogin();
    void logout();
    boolean isLoggedIn();
}
