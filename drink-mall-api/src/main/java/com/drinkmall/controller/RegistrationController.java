package com.drinkmall.controller;

import com.drinkmall.common.Result;
import com.drinkmall.dto.RegisterUserRequest;
import com.drinkmall.entity.User;
import com.drinkmall.service.PhaseOneCoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final PhaseOneCoreService phaseOneCoreService;

    @PostMapping
    public Result<User> register(@Valid @RequestBody RegisterUserRequest request) {
        return Result.success(phaseOneCoreService.registerUser(
                request.getOpenid(),
                request.getUnionid(),
                request.getInviterId(),
                request.getInviteCode(),
                request.getRegisterSource(),
                false
        ));
    }
}
