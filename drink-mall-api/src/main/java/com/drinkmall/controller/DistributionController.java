package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.DistributionLevelOverviewResponse;
import com.drinkmall.dto.InviterResponse;
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
@RequestMapping("/api/v1/distribution")
@RequiredArgsConstructor
public class DistributionController {

    private final UserService userService;
    private final PhaseOneCoreService phaseOneCoreService;

    @GetMapping("/level")
    @SaCheckLogin
    public Result<DistributionLevelOverviewResponse> getLevelOverview() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getDistributionLevelOverview(userId));
    }

    @GetMapping("/team")
    @SaCheckLogin
    public Result<TeamOverviewResponse> getMiniTeamOverview() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(phaseOneCoreService.getMiniTeamOverview(userId));
    }

    @GetMapping("/inviter")
    @SaCheckLogin
    public Result<InviterResponse> getInviter() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getInviter(userId));
    }

    @GetMapping("/direct")
    @SaCheckLogin
    public Result<List<TeamMemberResponse>> getDirectInvitees() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getDirectInvitees(userId));
    }

    @GetMapping("/indirect")
    @SaCheckLogin
    public Result<List<TeamMemberResponse>> getIndirectInvitees() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getIndirectInvitees(userId));
    }

    @GetMapping("/team-total")
    @SaCheckLogin
    public Result<Long> getTeamTotal() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userService.getTeamTotal(userId));
    }
}
