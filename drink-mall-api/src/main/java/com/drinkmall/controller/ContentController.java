package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.common.Result;
import com.drinkmall.dto.ContentResponse;
import com.drinkmall.entity.*;
import com.drinkmall.enums.AssetType;
import com.drinkmall.mapper.*;
import com.drinkmall.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final AnnouncementMapper announcementMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;
    private final ContentPurchaseMapper contentPurchaseMapper;
    private final SysConfigMapper sysConfigMapper;
    private final AssetService assetService;

    @GetMapping
    public Result<IPage<ContentResponse>> getContent(
            @RequestParam(defaultValue = "video") String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        if ("article".equals(type)) {
            IPage<ContentResponse> result = helpArticleMapper.selectPage(new Page<>(page, size),
                    new LambdaQueryWrapper<HelpArticle>()
                            .eq(HelpArticle::getStatus, 1)
                            .orderByAsc(HelpArticle::getSortOrder))
                    .convert(article -> ContentResponse.fromArticle(article, isPaid(userId, "article", article.getId()), price("article", article.getId())));
            return Result.success(result);
        }
        IPage<ContentResponse> result = videoMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Video>()
                        .eq(Video::getStatus, 1)
                        .orderByDesc(Video::getSortOrder))
                .convert(video -> ContentResponse.fromVideo(video, isPaid(userId, "video", video.getId()), price("video", video.getId())));
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
    public Result<ContentResponse> getDetail(@PathVariable Long id, @RequestParam(defaultValue = "video") String type) {
        Long userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        if ("article".equals(type)) {
            HelpArticle article = helpArticleMapper.selectById(id);
            if (article == null || article.getStatus() != 1) {
                return Result.success(null);
            }
            return Result.success(ContentResponse.fromArticle(article, isPaid(userId, "article", id), price("article", id)));
        }
        Video video = videoMapper.selectById(id);
        if (video == null || video.getStatus() != 1) {
            return Result.success(null);
        }
        return Result.success(ContentResponse.fromVideo(video, isPaid(userId, "video", id), price("video", id)));
    }

    @PostMapping("/{id}/buy")
    @SaCheckLogin
    @Transactional
    public Result<ContentResponse> buy(@PathVariable Long id, @RequestParam(defaultValue = "video") String type) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (isPaid(userId, type, id)) {
            return getDetail(id, type);
        }
        ensureExists(type, id);
        BigDecimal price = price(type, id);
        String idempotencyKey = "content_buy:" + userId + ":" + type + ":" + id;
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            assetService.deduct(userId, AssetType.BALANCE, price, "content_buy", id, "content", idempotencyKey, "buy paid content");
        }
        ContentPurchase purchase = new ContentPurchase();
        purchase.setUserId(userId);
        purchase.setContentType(type);
        purchase.setContentId(id);
        purchase.setPrice(price);
        purchase.setIdempotencyKey(idempotencyKey);
        purchase.setCreatedAt(LocalDateTime.now());
        contentPurchaseMapper.insert(purchase);
        return getDetail(id, type);
    }

    private void ensureExists(String type, Long id) {
        if ("article".equals(type)) {
            HelpArticle article = helpArticleMapper.selectById(id);
            if (article == null || article.getStatus() != 1) {
                throw new BusinessException(404, "内容不存在");
            }
            return;
        }
        Video video = videoMapper.selectById(id);
        if (video == null || video.getStatus() != 1) {
            throw new BusinessException(404, "内容不存在");
        }
    }

    private boolean isPaid(Long userId, String type, Long id) {
        if (userId == null) {
            return false;
        }
        Long count = contentPurchaseMapper.selectCount(new LambdaQueryWrapper<ContentPurchase>()
                .eq(ContentPurchase::getUserId, userId)
                .eq(ContentPurchase::getContentType, type)
                .eq(ContentPurchase::getContentId, id));
        return count != null && count > 0;
    }

    private BigDecimal price(String type, Long id) {
        SysConfig exact = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "content.price." + type + "." + id));
        if (exact != null && exact.getConfigValue() != null && !exact.getConfigValue().isBlank()) {
            return new BigDecimal(exact.getConfigValue());
        }
        SysConfig defaultConfig = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "content.default.price"));
        if (defaultConfig == null || defaultConfig.getConfigValue() == null || defaultConfig.getConfigValue().isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(defaultConfig.getConfigValue());
    }
}
