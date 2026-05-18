package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.common.Result;
import com.drinkmall.dto.ContentResponse;
import com.drinkmall.dto.PaymentMethodResponse;
import com.drinkmall.entity.Announcement;
import com.drinkmall.entity.ContentPurchase;
import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.SysConfig;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Video;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.AnnouncementMapper;
import com.drinkmall.mapper.ContentPurchaseMapper;
import com.drinkmall.mapper.HelpArticleMapper;
import com.drinkmall.mapper.SysConfigMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.VideoMapper;
import com.drinkmall.service.AssetService;
import com.drinkmall.service.support.ContentAccessDecision;
import com.drinkmall.service.support.ContentAccessPolicy;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private static final String TYPE_ARTICLE = "article";
    private static final String TYPE_VIDEO = "video";

    private final AnnouncementMapper announcementMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;
    private final ContentPurchaseMapper contentPurchaseMapper;
    private final SysConfigMapper sysConfigMapper;
    private final UserMapper userMapper;
    private final AssetService assetService;

    @GetMapping
    public Result<IPage<ContentResponse>> getContent(
            @RequestParam(defaultValue = TYPE_VIDEO) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = currentUserId();
        if (TYPE_ARTICLE.equals(type)) {
            IPage<ContentResponse> result = helpArticleMapper.selectPage(new Page<>(page, size),
                    new LambdaQueryWrapper<HelpArticle>()
                            .eq(HelpArticle::getStatus, 1)
                            .orderByAsc(HelpArticle::getSortOrder))
                    .convert(article -> articleResponse(article, userId));
            return Result.success(result);
        }
        IPage<ContentResponse> result = videoMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Video>()
                        .eq(Video::getStatus, 1)
                        .orderByDesc(Video::getSortOrder))
                .convert(video -> videoResponse(video, userId));
        return Result.success(result);
    }

    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        return Result.success(announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getStatus, 1)
                        .orderByDesc(Announcement::getCreatedAt)
        ));
    }

    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null || announcement.getStatus() != 1) {
            return Result.success(null);
        }
        return Result.success(announcement);
    }

    @GetMapping("/videos")
    public Result<Page<Video>> getVideos(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Video> pageParam = new Page<>(page, size);
        return Result.success(videoMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Video>()
                        .eq(Video::getStatus, 1)
                        .orderByDesc(Video::getSortOrder)
        ));
    }

    @GetMapping("/help")
    public Result<List<HelpArticle>> getHelpArticles(@RequestParam(required = false) String category) {
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<HelpArticle>()
                .eq(HelpArticle::getStatus, 1);
        if (category != null) {
            wrapper.eq(HelpArticle::getCategory, category);
        }
        wrapper.orderByAsc(HelpArticle::getSortOrder);
        return Result.success(helpArticleMapper.selectList(wrapper));
    }

    @GetMapping("/{id}")
    public Result<ContentResponse> getDetail(@PathVariable Long id, @RequestParam(defaultValue = TYPE_VIDEO) String type) {
        Long userId = currentUserId();
        if (TYPE_ARTICLE.equals(type)) {
            HelpArticle article = helpArticleMapper.selectById(id);
            if (article == null || article.getStatus() != 1) {
                return Result.success(null);
            }
            return Result.success(articleResponse(article, userId));
        }
        Video video = videoMapper.selectById(id);
        if (video == null || video.getStatus() != 1) {
            return Result.success(null);
        }
        return Result.success(videoResponse(video, userId));
    }

    @PostMapping("/{id}/buy")
    @SaCheckLogin
    @Transactional
    public Result<ContentResponse> buy(
            @PathVariable Long id,
            @RequestParam(defaultValue = TYPE_VIDEO) String type,
            @RequestBody(required = false) BuyContentRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (isPurchased(userId, type, id)) {
            return getDetail(id, type);
        }
        ContentRuntime runtime = runtime(type, id, userId);
        if (!runtime.access().isCanBuy()) {
            throw new BusinessException(400, runtime.access().getLockReason());
        }
        String payMethod = request == null ? null : request.resolvePaymentMethod();
        if (payMethod == null || payMethod.isBlank()) {
            throw new BusinessException(400, "请选择内容支付方式");
        }
        ensurePaymentMethodAllowed(payMethod, runtime.payMethods());
        pay(userId, type, id, runtime.price(), payMethod);
        insertPurchase(userId, type, id, runtime.price(), payMethod);
        return getDetail(id, type);
    }

    private ContentResponse videoResponse(Video video, Long userId) {
        boolean purchased = isPurchased(userId, TYPE_VIDEO, video.getId());
        BigDecimal price = price(video.getPaid(), video.getPrice());
        List<PaymentMethodResponse> payMethods = paymentMethods(video.getPaymentMethods());
        ContentAccessDecision access = access(userId, video.getWatchLevel(), Boolean.TRUE.equals(video.getPaid()), price, purchased);
        return ContentResponse.fromVideo(video, purchased, price, access, payMethods);
    }

    private ContentResponse articleResponse(HelpArticle article, Long userId) {
        boolean purchased = isPurchased(userId, TYPE_ARTICLE, article.getId());
        BigDecimal price = price(article.getPaid(), article.getPrice());
        List<PaymentMethodResponse> payMethods = paymentMethods(article.getPaymentMethods());
        ContentAccessDecision access = access(userId, article.getWatchLevel(), Boolean.TRUE.equals(article.getPaid()), price, purchased);
        return ContentResponse.fromArticle(article, purchased, price, access, payMethods);
    }

    private ContentRuntime runtime(String type, Long id, Long userId) {
        if (TYPE_ARTICLE.equals(type)) {
            HelpArticle article = helpArticleMapper.selectById(id);
            if (article == null || article.getStatus() != 1) {
                throw new BusinessException(404, "内容不存在");
            }
            boolean purchased = isPurchased(userId, type, id);
            BigDecimal price = price(article.getPaid(), article.getPrice());
            return new ContentRuntime(
                    access(userId, article.getWatchLevel(), Boolean.TRUE.equals(article.getPaid()), price, purchased),
                    price,
                    paymentMethods(article.getPaymentMethods())
            );
        }
        Video video = videoMapper.selectById(id);
        if (video == null || video.getStatus() != 1) {
            throw new BusinessException(404, "内容不存在");
        }
        boolean purchased = isPurchased(userId, type, id);
        BigDecimal price = price(video.getPaid(), video.getPrice());
        return new ContentRuntime(
                access(userId, video.getWatchLevel(), Boolean.TRUE.equals(video.getPaid()), price, purchased),
                price,
                paymentMethods(video.getPaymentMethods())
        );
    }

    private ContentAccessDecision access(Long userId, String watchLevel, boolean paidContent, BigDecimal price, boolean purchased) {
        return ContentAccessPolicy.evaluate(
                userLevel(userId),
                watchLevel == null || watchLevel.isBlank() ? configValue("content.default.min_level", "normal") : watchLevel,
                paidContent,
                price,
                purchased,
                Boolean.parseBoolean(configValue("content.purchase.allow_below_level", "false")),
                this::levelOrder,
                this::levelName
        );
    }

    private void pay(Long userId, String type, Long id, BigDecimal price, String payMethod) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        String idempotencyKey = "content_buy:" + userId + ":" + type + ":" + id + ":" + payMethod;
        switch (payMethod) {
            case "balance" -> assetService.deduct(userId, AssetType.BALANCE, price, "content_buy", id, "content", idempotencyKey, "buy paid content");
            case "df" -> assetService.deduct(userId, AssetType.DF, price, "content_buy", id, "content", idempotencyKey, "buy paid content");
            case "wine_bean" -> assetService.deduct(userId, AssetType.WINE_BEAN, price, "content_buy", id, "content", idempotencyKey, "buy paid content");
            case "points" -> assetService.deduct(userId, AssetType.POINTS, price, "content_buy", id, "content", idempotencyKey, "buy paid content");
            case "online" -> {
                // Existing phase-one online payment flow marks paid synchronously; keep content consistent with that mock flow.
            }
            default -> throw new BusinessException(400, "内容暂不支持当前支付方式");
        }
    }

    private void insertPurchase(Long userId, String type, Long id, BigDecimal price, String payMethod) {
        ContentPurchase purchase = new ContentPurchase();
        purchase.setUserId(userId);
        purchase.setContentType(type);
        purchase.setContentId(id);
        purchase.setPrice(price);
        purchase.setPaymentMethod(payMethod);
        purchase.setIdempotencyKey("content_buy:" + userId + ":" + type + ":" + id);
        purchase.setCreatedAt(LocalDateTime.now());
        contentPurchaseMapper.insert(purchase);
    }

    private void ensurePaymentMethodAllowed(String payMethod, List<PaymentMethodResponse> payMethods) {
        boolean supported = payMethods.stream().anyMatch(method -> payMethod.equals(method.getCode()));
        if (!supported) {
            throw new BusinessException(400, "内容不支持当前支付方式");
        }
    }

    private boolean isPurchased(Long userId, String type, Long id) {
        if (userId == null) {
            return false;
        }
        Long count = contentPurchaseMapper.selectCount(new LambdaQueryWrapper<ContentPurchase>()
                .eq(ContentPurchase::getUserId, userId)
                .eq(ContentPurchase::getContentType, type)
                .eq(ContentPurchase::getContentId, id));
        return count != null && count > 0;
    }

    private BigDecimal price(Boolean paid, BigDecimal price) {
        if (!Boolean.TRUE.equals(paid)) {
            return BigDecimal.ZERO;
        }
        return price == null ? BigDecimal.ZERO : price;
    }

    private List<PaymentMethodResponse> paymentMethods(String codes) {
        String resolved = codes == null || codes.isBlank()
                ? configValue("content.default.payment_methods", "balance")
                : codes;
        return PaymentMethodResponse.fromCodes(resolved);
    }

    private Long currentUserId() {
        return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
    }

    private String userLevel(Long userId) {
        if (userId == null) {
            return "normal";
        }
        User user = userMapper.selectById(userId);
        return user == null || user.getDistributionLevel() == null || user.getDistributionLevel().isBlank()
                ? "normal"
                : user.getDistributionLevel();
    }

    private Integer levelOrder(String level) {
        return Integer.valueOf(requiredConfigValue("level." + level + ".order"));
    }

    private String levelName(String level) {
        return configValue("level." + level + ".name", level);
    }

    private String configValue(String key, String defaultValue) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            return defaultValue;
        }
        return config.getConfigValue().trim();
    }

    private String requiredConfigValue(String key) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().isBlank()) {
            throw new BusinessException(500, "系统配置缺失: " + key);
        }
        return config.getConfigValue().trim();
    }

    private record ContentRuntime(ContentAccessDecision access, BigDecimal price, List<PaymentMethodResponse> payMethods) {
    }

    @Data
    public static class BuyContentRequest {
        private String paymentMethod;
        private String payMethod;

        public String resolvePaymentMethod() {
            return paymentMethod == null || paymentMethod.isBlank() ? payMethod : paymentMethod;
        }
    }
}
