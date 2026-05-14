package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.TeamMemberResponse;
import com.drinkmall.dto.TeamOverviewResponse;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {

    private final PhaseOneCoreService phaseOneCoreService;
    private final UserService userService;

    @GetMapping("/overview")
    @SaCheckLogin
    public Result<TeamOverviewResponse> overview() {
        return Result.success(phaseOneCoreService.getMiniTeamOverview(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/directs")
    @SaCheckLogin
    public Result<List<TeamMemberResponse>> directs() {
        return Result.success(userService.getDirectInvitees(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/indirects")
    @SaCheckLogin
    public Result<List<TeamMemberResponse>> indirects() {
        return Result.success(userService.getIndirectInvitees(StpUtil.getLoginIdAsLong()));
    }
}
