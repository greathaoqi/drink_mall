package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.*;
import com.drinkmall.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @SaCheckLogin
    public Result<CartResponse> getCart() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(cartService.getCart(userId));
    }

    @PostMapping
    @SaCheckLogin
    public Result<Void> addToCart(@Valid @RequestBody AddToCartRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.addToCart(userId, request);
        return Result.success(null);
    }

    @PutMapping("/{cartId}/quantity")
    @SaCheckLogin
    public Result<Void> updateQuantity(@PathVariable Long cartId, @RequestParam Integer quantity) {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.updateQuantity(userId, cartId, quantity);
        return Result.success(null);
    }

    @DeleteMapping("/{cartId}")
    @SaCheckLogin
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.removeFromCart(userId, cartId);
        return Result.success(null);
    }

    @PutMapping("/{cartId}/selected")
    @SaCheckLogin
    public Result<Void> updateSelected(@PathVariable Long cartId, @RequestParam Boolean selected) {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.updateSelected(userId, cartId, selected);
        return Result.success(null);
    }

    @PutMapping("/select-all")
    @SaCheckLogin
    public Result<Void> selectAll(@RequestParam Boolean selected) {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.selectAll(userId, selected);
        return Result.success(null);
    }

    @DeleteMapping
    @SaCheckLogin
    public Result<Void> clearCart() {
        Long userId = StpUtil.getLoginIdAsLong();
        cartService.clearCart(userId);
        return Result.success(null);
    }
}
