package com.drinkmall.service;

import com.drinkmall.dto.PayResponse;

import java.math.BigDecimal;

public interface ContentPurchaseService {
    /**
     * Create a prepay order for content purchase.
     * Returns existing pending order if one exists for this user+content.
     * Per D-CPAY-11 and D-CPAY-12: blocks duplicate purchases.
     */
    PayResponse createPrepayOrder(Long userId, String contentType, Long contentId, BigDecimal price);

    /**
     * Confirm payment from WeChat Pay callback.
     * Per D-CPAY-01: dispatched from PayController when orderNo starts with "CP".
     */
    void confirmPaymentCallback(String orderNo, BigDecimal paidAmount, String paymentNo);

    /**
     * Check if user has purchased content.
     */
    boolean hasPurchased(Long userId, String contentType, Long contentId);

    /**
     * Cancel expired pending orders (called by scheduler).
     * Per D-CPAY-06: 30 minute timeout.
     */
    void cancelExpiredOrders();
}