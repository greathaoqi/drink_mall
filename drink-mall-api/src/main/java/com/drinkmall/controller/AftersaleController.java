package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.dto.CreateAftersaleRequest;
import com.drinkmall.entity.Aftersale;
import com.drinkmall.service.AftersaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/aftersale")
@RequiredArgsConstructor
public class AftersaleController {

    private final AftersaleService aftersaleService;

    @PostMapping
    @SaCheckLogin
    public Result<Aftersale> apply(@Valid @RequestBody CreateAftersaleRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(aftersaleService.applyAftersale(
            userId, request.getOrderId(),
            request.getType(), request.getReason(), request.getDescription()
        ));
    }

    @GetMapping({"", "/list"})
    @SaCheckLogin
    public Result<Page<Aftersale>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(aftersaleService.getUserAftersales(userId, page, size));
    }

    @GetMapping("/{id}")
    @SaCheckLogin
    public Result<Aftersale> detail(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(aftersaleService.getAftersaleDetail(userId, id));
    }
}
