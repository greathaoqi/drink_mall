package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.RealNameReviewRequest;
import com.drinkmall.entity.RealNameVerification;
import com.drinkmall.service.RealNameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/real-name")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminRealNameController {

    private final RealNameService realNameService;

    @GetMapping("/list")
    public Result<Page<RealNameVerification>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(realNameService.getReviewPage(status, keyword, page, size));
    }

    @PostMapping("/{verificationId}/review")
    public Result<Void> review(@PathVariable Long verificationId, @Valid @RequestBody RealNameReviewRequest request) {
        String loginId = String.valueOf(StpUtil.getLoginId());
        Long adminId = loginId.startsWith("admin:") ? Long.valueOf(loginId.substring("admin:".length())) : null;
        realNameService.review(verificationId, adminId, Boolean.TRUE.equals(request.getApproved()), request.getRejectReason());
        return Result.success(null);
    }
}
