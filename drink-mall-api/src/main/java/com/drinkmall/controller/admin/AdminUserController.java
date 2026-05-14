package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.InvitationCodeCreateRequest;
import com.drinkmall.dto.RegisterUserRequest;
import com.drinkmall.entity.InvitationCode;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.User;
import com.drinkmall.mapper.InvitationCodeMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.service.PhaseOneCoreService;
import com.drinkmall.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final PhaseOneCoreService phaseOneCoreService;
    private final InvitationCodeMapper invitationCodeMapper;
    private final OperationLogMapper operationLogMapper;

    @GetMapping("/list")
    public Result<Page<User>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer ageVerified,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminUserService.getUsers(keyword, ageVerified, page, size));
    }

    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        Long adminId = currentAdminId();
        writeLog(adminId, "user", "detail_query", String.valueOf(userId), "query user detail");
        return Result.success(adminUserService.getUserDetail(userId));
    }

    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response, @RequestParam(required = false) String keyword) {
        adminUserService.exportUsers(response, keyword);
    }

    @PostMapping("/seed-accounts")
    public Result<User> createSeedAccount(@Valid @RequestBody RegisterUserRequest request) {
        Long adminId = currentAdminId();
        User user = phaseOneCoreService.registerUser(
                request.getOpenid(),
                request.getUnionid(),
                null,
                null,
                "admin_seed",
                true
        );
        writeLog(adminId, "user", "create_seed", String.valueOf(user.getId()), "openid=" + user.getOpenid());
        return Result.success(user);
    }

    @PostMapping("/invite-codes")
    public Result<InvitationCode> createInvitationCode(@Valid @RequestBody InvitationCodeCreateRequest request) {
        Long adminId = currentAdminId();
        InvitationCode code = phaseOneCoreService.createInvitationCode(request.getOwnerUserId(), adminId, request.getSource());
        writeLog(adminId, "invite_code", "create", String.valueOf(code.getId()), "code=" + code.getCode() + ", ownerUserId=" + code.getOwnerUserId());
        return Result.success(code);
    }

    @GetMapping("/invite-codes")
    public Result<Page<InvitationCode>> getInvitationCodes(
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        LambdaQueryWrapper<InvitationCode> wrapper = new LambdaQueryWrapper<InvitationCode>().orderByDesc(InvitationCode::getCreatedAt);
        if (ownerUserId != null) {
            wrapper.eq(InvitationCode::getOwnerUserId, ownerUserId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(InvitationCode::getStatus, status);
        }
        return Result.success(invitationCodeMapper.selectPage(new Page<>(page, size), wrapper));
    }

    private Long currentAdminId() {
        String loginId = String.valueOf(StpUtil.getLoginId());
        return loginId.startsWith("admin:") ? Long.valueOf(loginId.substring("admin:".length())) : null;
    }

    private void writeLog(Long adminId, String module, String action, String targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
