package com.drinkmall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.PayResponse;
import com.drinkmall.entity.ContentPurchase;
import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.Video;
import com.drinkmall.enums.ContentPurchaseStatus;
import com.drinkmall.mapper.ContentPurchaseMapper;
import com.drinkmall.mapper.HelpArticleMapper;
import com.drinkmall.mapper.VideoMapper;
import com.drinkmall.service.ContentPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentPurchaseServiceImpl implements ContentPurchaseService {

    private static final String ORDER_PREFIX = "CP";
    private static final int EXPIRE_MINUTES = 30;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ContentPurchaseMapper contentPurchaseMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;

    // Simple sequence counter for order number generation (in production, use distributed sequence)
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    @Transactional
    public PayResponse createPrepayOrder(Long userId, String contentType, Long contentId, BigDecimal price) {
        // D-CPAY-11: Block duplicate purchases - check if user already paid for this content
        if (hasPurchased(userId, contentType, contentId)) {
            throw new BusinessException(400, "已购买该内容");
        }

        // Check for existing pending order - return it for idempotency
        ContentPurchase existingPending = contentPurchaseMapper.selectOne(new LambdaQueryWrapper<ContentPurchase>()
                .eq(ContentPurchase::getUserId, userId)
                .eq(ContentPurchase::getContentType, contentType)
                .eq(ContentPurchase::getContentId, contentId)
                .eq(ContentPurchase::getStatus, ContentPurchaseStatus.PENDING.getCode()));

        if (existingPending != null) {
            log.info("Returning existing pending content purchase order: {}", existingPending.getOrderNo());
            return PayResponse.builder()
                    .orderNo(existingPending.getOrderNo())
                    .prepayId(existingPending.getPaymentNo())
                    .build();
        }

        // Validate content exists and get price
        BigDecimal contentPrice = validateAndGetPrice(contentType, contentId);
        if (contentPrice == null || contentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "内容价格无效");
        }
        if (contentPrice.compareTo(price) != 0) {
            throw new BusinessException(400, "内容价格已变更，请刷新后重试");
        }

        // Generate CP-prefixed order number: CP + YYYYMMDD + 5-digit sequence
        String orderNo = generateOrderNo();

        // Create content purchase with pending status
        ContentPurchase purchase = new ContentPurchase();
        purchase.setUserId(userId);
        purchase.setContentType(contentType);
        purchase.setContentId(contentId);
        purchase.setPrice(price);
        purchase.setPaymentMethod("online");
        purchase.setStatus(ContentPurchaseStatus.PENDING.getCode());
        purchase.setOrderNo(orderNo);
        purchase.setIdempotencyKey("content_buy:" + userId + ":" + contentType + ":" + contentId);
        purchase.setCreatedAt(LocalDateTime.now());
        contentPurchaseMapper.insert(purchase);

        log.info("Created content purchase prepay order: {} for user {} content {}:{}", orderNo, userId, contentType, contentId);

        // Return PayResponse (for real WeChat Pay, this would call WxPayService to create prepay)
        // For Phase 1 compatibility, return orderNo with mock prepayId
        return PayResponse.builder()
                .orderNo(orderNo)
                .prepayId("MOCK_PREPAY_" + orderNo)
                .build();
    }

    @Override
    @Transactional
    public void confirmPaymentCallback(String orderNo, BigDecimal paidAmount, String paymentNo) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new BusinessException(400, "订单号不能为空");
        }
        if (paidAmount == null) {
            throw new BusinessException(400, "支付金额不能为空");
        }
        if (paymentNo == null || paymentNo.isBlank()) {
            throw new BusinessException(400, "微信支付流水号不能为空");
        }

        ContentPurchase purchase = contentPurchaseMapper.selectOne(new LambdaQueryWrapper<ContentPurchase>()
                .eq(ContentPurchase::getOrderNo, orderNo));
        if (purchase == null) {
            throw new BusinessException(404, "内容购买订单不存在");
        }

        // D-CPAY-01, T-6-02: Validate paid amount matches stored price
        if (purchase.getPrice() == null || purchase.getPrice().compareTo(paidAmount) != 0) {
            throw new BusinessException(400, "支付金额不一致");
        }

        // Already paid - idempotent return (replay attack protection)
        if (!ContentPurchaseStatus.PENDING.getCode().equals(purchase.getStatus())) {
            log.info("Content purchase order {} already processed with status: {}", orderNo, purchase.getStatus());
            return;
        }

        // Check if paymentNo already processed (additional replay protection)
        if (purchase.getPaymentNo() != null && paymentNo.equals(purchase.getPaymentNo())) {
            log.info("Content purchase order {} already has same paymentNo: {}", orderNo, paymentNo);
            return;
        }

        // Update status with optimistic locking: only update if status is still 'pending'
        LocalDateTime paidAt = LocalDateTime.now();
        int updated = contentPurchaseMapper.update(null, new UpdateWrapper<ContentPurchase>()
                .eq("id", purchase.getId())
                .eq("status", ContentPurchaseStatus.PENDING.getCode())
                .set("status", ContentPurchaseStatus.PAID.getCode())
                .set("payment_no", paymentNo)
                .set("payment_time", paidAt));

        if (updated == 0) {
            log.warn("Content purchase order {} status changed before callback, skipping", orderNo);
            return;
        }

        log.info("Confirmed content purchase payment: orderNo={}, paymentNo={}, amount={}", orderNo, paymentNo, paidAmount);
    }

    @Override
    public boolean hasPurchased(Long userId, String contentType, Long contentId) {
        if (userId == null) {
            return false;
        }
        Long count = contentPurchaseMapper.selectCount(new LambdaQueryWrapper<ContentPurchase>()
                .eq(ContentPurchase::getUserId, userId)
                .eq(ContentPurchase::getContentType, contentType)
                .eq(ContentPurchase::getContentId, contentId)
                .eq(ContentPurchase::getStatus, ContentPurchaseStatus.PAID.getCode()));
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(EXPIRE_MINUTES);

        int cancelled = contentPurchaseMapper.update(null, new UpdateWrapper<ContentPurchase>()
                .eq("status", ContentPurchaseStatus.PENDING.getCode())
                .lt("created_at", expireTime)
                .set("status", ContentPurchaseStatus.EXPIRED.getCode()));

        if (cancelled > 0) {
            log.info("Cancelled {} expired content purchase orders (older than {} minutes)", cancelled, EXPIRE_MINUTES);
        }
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMAT);
        int seq = sequence.updateAndGet(current -> current >= 99999 ? 1 : current + 1);
        return ORDER_PREFIX + dateStr + String.format("%05d", seq);
    }

    private BigDecimal validateAndGetPrice(String contentType, Long contentId) {
        if ("video".equals(contentType)) {
            Video video = videoMapper.selectById(contentId);
            if (video == null || video.getStatus() == null || video.getStatus() != 1) {
                throw new BusinessException(404, "视频内容不存在");
            }
            return Boolean.TRUE.equals(video.getPaid()) ? video.getPrice() : BigDecimal.ZERO;
        } else if ("article".equals(contentType)) {
            HelpArticle article = helpArticleMapper.selectById(contentId);
            if (article == null || article.getStatus() == null || article.getStatus() != 1) {
                throw new BusinessException(404, "文章内容不存在");
            }
            return Boolean.TRUE.equals(article.getPaid()) ? article.getPrice() : BigDecimal.ZERO;
        } else {
            throw new BusinessException(400, "不支持的内容类型: " + contentType);
        }
    }
}