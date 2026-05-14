package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.dto.PayOrderRequest;
import com.drinkmall.dto.PayResponse;
import com.drinkmall.dto.PaymentMethodResponse;
import com.drinkmall.service.OrderService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @GetMapping("/methods")
    @SaCheckLogin
    public Result<List<PaymentMethodResponse>> methods() {
        return Result.success(PaymentMethodResponse.all());
    }

    @PostMapping("/pay")
    @SaCheckLogin
    public Result<PayResponse> pay(@Valid @RequestBody MiniPayRequest request) {
        PayOrderRequest payOrderRequest = new PayOrderRequest();
        payOrderRequest.setPaymentMethod(request.getPaymentMethod());
        payOrderRequest.setPaymentNo(request.getPaymentNo());
        return Result.success(orderService.payOrder(StpUtil.getLoginIdAsLong(), request.getOrderId(), payOrderRequest));
    }

    @Data
    public static class MiniPayRequest {
        private Long orderId;
        private String paymentMethod;
        private String paymentNo;
    }
}
