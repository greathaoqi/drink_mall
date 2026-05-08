package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.*;
import com.drinkmall.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @SaCheckLogin
    public Result<List<AddressResponse>> getAddresses() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(addressService.getAddresses(userId));
    }

    @GetMapping("/{addressId}")
    @SaCheckLogin
    public Result<AddressResponse> getAddress(@PathVariable Long addressId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(addressService.getAddress(userId, addressId));
    }

    @GetMapping("/default")
    @SaCheckLogin
    public Result<AddressResponse> getDefaultAddress() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(addressService.getDefaultAddress(userId));
    }

    @PostMapping
    @SaCheckLogin
    public Result<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(addressService.addAddress(userId, request));
    }

    @PutMapping("/{addressId}")
    @SaCheckLogin
    public Result<AddressResponse> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(addressService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @SaCheckLogin
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        Long userId = StpUtil.getLoginIdAsLong();
        addressService.deleteAddress(userId, addressId);
        return Result.success(null);
    }

    @PutMapping("/{addressId}/default")
    @SaCheckLogin
    public Result<Void> setDefault(@PathVariable Long addressId) {
        Long userId = StpUtil.getLoginIdAsLong();
        addressService.setDefault(userId, addressId);
        return Result.success(null);
    }
}
