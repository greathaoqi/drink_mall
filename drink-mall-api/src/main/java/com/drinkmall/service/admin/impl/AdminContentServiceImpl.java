package com.drinkmall.service.admin.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.ContentAnalyticsResponse;
import com.drinkmall.dto.ContentPurchaseRecordResponse;
import com.drinkmall.entity.Announcement;
import com.drinkmall.entity.Banner;
import com.drinkmall.entity.ContentCategory;
import com.drinkmall.entity.ContentPurchase;
import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.User;
import com.drinkmall.entity.Video;
import com.drinkmall.mapper.AnnouncementMapper;
import com.drinkmall.mapper.BannerMapper;
import com.drinkmall.mapper.ContentCategoryMapper;
import com.drinkmall.mapper.ContentPurchaseMapper;
import com.drinkmall.mapper.HelpArticleMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.mapper.VideoMapper;
import com.drinkmall.service.admin.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private static final String BUSINESS_SOURCE = "admin_content";

    private final BannerMapper bannerMapper;
    private final AnnouncementMapper announcementMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;
    private final OperationLogMapper operationLogMapper;
    private final ContentCategoryMapper contentCategoryMapper;
    private final ContentPurchaseMapper contentPurchaseMapper;
    private final UserMapper userMapper;

    @Override
    public List<Banner> getBanners(String location) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder);
        if (location != null) wrapper.eq(Banner::getLocation, location);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Banner createBanner(Banner banner) {
        banner.setCreatedAt(LocalDateTime.now());
        bannerMapper.insert(banner);
        logOperation("banner", "create", banner.getId(), null, bannerSummary(banner));
        return banner;
    }

    @Override
    @Transactional
    public Banner updateBanner(Banner banner) {
        Banner before = bannerMapper.selectById(banner.getId());
        banner.setUpdatedAt(LocalDateTime.now());
        bannerMapper.updateById(banner);
        logOperation("banner", "update", banner.getId(), bannerSummary(before), bannerSummary(banner));
        return banner;
    }

    @Override
    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner before = bannerMapper.selectById(bannerId);
        bannerMapper.deleteById(bannerId);
        logOperation("banner", "delete", bannerId, bannerSummary(before), null);
    }

    @Override
    public Page<Announcement> getAnnouncements(Integer status, Integer page, Integer size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>().orderByDesc(Announcement::getCreatedAt);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        return announcementMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcementMapper.insert(announcement);
        logOperation("announcement", "create", announcement.getId(), null, announcementSummary(announcement));
        return announcement;
    }

    @Override
    @Transactional
    public Announcement updateAnnouncement(Announcement announcement) {
        Announcement before = announcementMapper.selectById(announcement.getId());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateById(announcement);
        logOperation("announcement", "update", announcement.getId(), announcementSummary(before), announcementSummary(announcement));
        return announcement;
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long announcementId) {
        Announcement before = announcementMapper.selectById(announcementId);
        announcementMapper.deleteById(announcementId);
        logOperation("announcement", "delete", announcementId, announcementSummary(before), null);
    }

    @Override
    public Page<Video> getVideos(Integer status, Integer page, Integer size) {
        Page<Video> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>().orderByDesc(Video::getCreatedAt);
        if (status != null) wrapper.eq(Video::getStatus, status);
        return videoMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public Video createVideo(Video video) {
        video.setCreatedAt(LocalDateTime.now());
        videoMapper.insert(video);
        logOperation("video", "create", video.getId(), null, videoSummary(video));
        return video;
    }

    @Override
    @Transactional
    public Video updateVideo(Video video) {
        Video before = videoMapper.selectById(video.getId());
        video.setUpdatedAt(LocalDateTime.now());
        videoMapper.updateById(video);
        logOperation("video", "update", video.getId(), videoSummary(before), videoSummary(video));
        return video;
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId) {
        Video before = videoMapper.selectById(videoId);
        videoMapper.deleteById(videoId);
        logOperation("video", "delete", videoId, videoSummary(before), null);
    }

    @Override
    public List<HelpArticle> getHelpArticles(String category) {
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<HelpArticle>().orderByAsc(HelpArticle::getSortOrder);
        if (category != null) wrapper.eq(HelpArticle::getCategory, category);
        return helpArticleMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public HelpArticle createHelpArticle(HelpArticle article) {
        article.setCreatedAt(LocalDateTime.now());
        helpArticleMapper.insert(article);
        logOperation("help_article", "create", article.getId(), null, articleSummary(article));
        return article;
    }

    @Override
    @Transactional
    public HelpArticle updateHelpArticle(HelpArticle article) {
        HelpArticle before = helpArticleMapper.selectById(article.getId());
        article.setUpdatedAt(LocalDateTime.now());
        helpArticleMapper.updateById(article);
        logOperation("help_article", "update", article.getId(), articleSummary(before), articleSummary(article));
        return article;
    }

    @Override
    @Transactional
    public void deleteHelpArticle(Long articleId) {
        HelpArticle before = helpArticleMapper.selectById(articleId);
        helpArticleMapper.deleteById(articleId);
        logOperation("help_article", "delete", articleId, articleSummary(before), null);
    }

    @Override
    public List<ContentCategory> getCategories() {
        return contentCategoryMapper.selectList(
                new LambdaQueryWrapper<ContentCategory>()
                        .eq(ContentCategory::getStatus, 1)
                        .orderByAsc(ContentCategory::getSortOrder)
        );
    }

    @Override
    @Transactional
    public ContentCategory createCategory(ContentCategory category) {
        category.setStatus(1);
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        category.setCreatedAt(LocalDateTime.now());
        contentCategoryMapper.insert(category);
        logOperation("content_category", "create", category.getId(), null, categorySummary(category));
        return category;
    }

    @Override
    @Transactional
    public ContentCategory updateCategory(ContentCategory category) {
        ContentCategory before = contentCategoryMapper.selectById(category.getId());
        category.setUpdatedAt(LocalDateTime.now());
        contentCategoryMapper.updateById(category);
        logOperation("content_category", "update", category.getId(), categorySummary(before), categorySummary(category));
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // Soft delete - set status to 0
        ContentCategory category = contentCategoryMapper.selectById(categoryId);
        if (category != null) {
            category.setStatus(0);
            category.setUpdatedAt(LocalDateTime.now());
            contentCategoryMapper.updateById(category);
            logOperation("content_category", "delete", categoryId, categorySummary(category), null);
        }
    }

    private String categorySummary(ContentCategory category) {
        if (category == null) return null;
        return "id=" + category.getId()
                + ", name=" + category.getName()
                + ", sortOrder=" + category.getSortOrder()
                + ", status=" + category.getStatus();
    }

    private String bannerSummary(Banner banner) {
        if (banner == null) return null;
        return "id=" + banner.getId()
                + ", title=" + banner.getTitle()
                + ", location=" + banner.getLocation()
                + ", status=" + banner.getStatus()
                + ", sortOrder=" + banner.getSortOrder();
    }

    private String announcementSummary(Announcement announcement) {
        if (announcement == null) return null;
        return "id=" + announcement.getId()
                + ", title=" + announcement.getTitle()
                + ", status=" + announcement.getStatus()
                + ", publisher=" + announcement.getPublisher();
    }

    private String videoSummary(Video video) {
        if (video == null) return null;
        return "id=" + video.getId()
                + ", title=" + video.getTitle()
                + ", status=" + video.getStatus()
                + ", watchLevel=" + video.getWatchLevel()
                + ", paid=" + video.getPaid()
                + ", price=" + video.getPrice()
                + ", paymentMethods=" + video.getPaymentMethods();
    }

    private String articleSummary(HelpArticle article) {
        if (article == null) return null;
        return "id=" + article.getId()
                + ", title=" + article.getTitle()
                + ", category=" + article.getCategory()
                + ", status=" + article.getStatus()
                + ", watchLevel=" + article.getWatchLevel()
                + ", paid=" + article.getPaid()
                + ", price=" + article.getPrice()
                + ", paymentMethods=" + article.getPaymentMethods();
    }

    private void logOperation(String module, String action, Long targetId, String before, String after) {
        OperationLog log = new OperationLog();
        log.setAdminUserId(currentAdminId());
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId == null ? null : String.valueOf(targetId));
        log.setDetail("source=" + BUSINESS_SOURCE + "; before=" + before + "; after=" + after);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    private Long currentAdminId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public IPage<ContentPurchaseRecordResponse> getPurchaseRecords(
            String contentType, String status, Long userId,
            Integer page, Integer size) {
        LambdaQueryWrapper<ContentPurchase> wrapper = new LambdaQueryWrapper<>();
        if (contentType != null && !contentType.isBlank()) {
            wrapper.eq(ContentPurchase::getContentType, contentType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(ContentPurchase::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(ContentPurchase::getUserId, userId);
        }
        wrapper.orderByDesc(ContentPurchase::getCreatedAt);

        Page<ContentPurchase> purchasePage = contentPurchaseMapper.selectPage(
            new Page<>(page, size), wrapper);

        return purchasePage.convert(this::toRecordResponse);
    }

    private ContentPurchaseRecordResponse toRecordResponse(ContentPurchase purchase) {
        User user = userMapper.selectById(purchase.getUserId());
        String contentTitle = getContentTitle(purchase.getContentType(), purchase.getContentId());

        return ContentPurchaseRecordResponse.builder()
            .id(purchase.getId())
            .userId(purchase.getUserId())
            .userNickname(user != null ? user.getNickname() : null)
            .userPhone(user != null ? user.getPhone() : null)
            .contentType(purchase.getContentType())
            .contentId(purchase.getContentId())
            .contentTitle(contentTitle)
            .price(purchase.getPrice())
            .paymentMethod(purchase.getPaymentMethod())
            .status(purchase.getStatus())
            .orderNo(purchase.getOrderNo())
            .paymentNo(purchase.getPaymentNo())
            .paymentTime(purchase.getPaymentTime())
            .createdAt(purchase.getCreatedAt())
            .build();
    }

    private String getContentTitle(String contentType, Long contentId) {
        if ("article".equals(contentType)) {
            HelpArticle article = helpArticleMapper.selectById(contentId);
            return article != null ? article.getTitle() : null;
        }
        Video video = videoMapper.selectById(contentId);
        return video != null ? video.getTitle() : null;
    }

    @Override
    public ContentAnalyticsResponse getAnalytics() {
        // Total revenue and purchases
        LambdaQueryWrapper<ContentPurchase> paidWrapper = new LambdaQueryWrapper<ContentPurchase>()
            .eq(ContentPurchase::getStatus, "paid");
        List<ContentPurchase> paidPurchases = contentPurchaseMapper.selectList(paidWrapper);

        BigDecimal totalRevenue = paidPurchases.stream()
            .map(ContentPurchase::getPrice)
            .filter(p -> p != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalPurchases = paidPurchases.size();
        long totalUsers = paidPurchases.stream()
            .map(ContentPurchase::getUserId)
            .distinct()
            .count();

        // Revenue by type
        Map<String, BigDecimal> revenueByType = paidPurchases.stream()
            .collect(Collectors.groupingBy(
                ContentPurchase::getContentType,
                Collectors.reducing(BigDecimal.ZERO,
                    p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO,
                    BigDecimal::add)
            ));
        Map<String, Integer> purchasesByType = paidPurchases.stream()
            .collect(Collectors.groupingBy(
                ContentPurchase::getContentType,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));

        // Top content
        Map<String, List<ContentPurchase>> byContent = paidPurchases.stream()
            .collect(Collectors.groupingBy(p -> p.getContentType() + ":" + p.getContentId()));
        List<ContentAnalyticsResponse.TopContent> topContent = byContent.entrySet().stream()
            .sorted((a, b) -> b.getValue().size() - a.getValue().size())
            .limit(10)
            .map(entry -> {
                String[] parts = entry.getKey().split(":");
                String type = parts[0];
                Long id = Long.parseLong(parts[1]);
                return ContentAnalyticsResponse.TopContent.builder()
                    .contentType(type)
                    .contentId(id)
                    .title(getContentTitle(type, id))
                    .purchaseCount(entry.getValue().size())
                    .revenue(entry.getValue().stream()
                        .map(ContentPurchase::getPrice)
                        .filter(p -> p != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .build();
            })
            .collect(Collectors.toList());

        // Daily stats (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<ContentPurchase> recentPurchases = paidPurchases.stream()
            .filter(p -> p.getPaymentTime() != null && p.getPaymentTime().isAfter(thirtyDaysAgo))
            .collect(Collectors.toList());
        Map<LocalDate, List<ContentPurchase>> byDate = recentPurchases.stream()
            .filter(p -> p.getPaymentTime() != null)
            .collect(Collectors.groupingBy(p -> p.getPaymentTime().toLocalDate()));
        List<ContentAnalyticsResponse.DailyStats> dailyStats = byDate.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> ContentAnalyticsResponse.DailyStats.builder()
                .date(entry.getKey().toString())
                .purchaseCount(entry.getValue().size())
                .revenue(entry.getValue().stream()
                    .map(ContentPurchase::getPrice)
                    .filter(p -> p != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build())
            .collect(Collectors.toList());

        return ContentAnalyticsResponse.builder()
            .totalRevenue(totalRevenue)
            .totalPurchases(totalPurchases)
            .totalUsers((int) totalUsers)
            .revenueByType(revenueByType)
            .purchasesByType(purchasesByType)
            .topContent(topContent)
            .dailyStats(dailyStats)
            .build();
    }
}
