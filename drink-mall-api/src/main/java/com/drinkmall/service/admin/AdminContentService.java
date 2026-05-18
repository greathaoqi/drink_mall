package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.dto.ContentAnalyticsResponse;
import com.drinkmall.dto.ContentPurchaseRecordResponse;
import com.drinkmall.entity.*;
import java.util.List;

public interface AdminContentService {
    List<Banner> getBanners(String location);
    Banner createBanner(Banner banner);
    Banner updateBanner(Banner banner);
    void deleteBanner(Long bannerId);
    Page<Announcement> getAnnouncements(Integer status, Integer page, Integer size);
    Announcement createAnnouncement(Announcement announcement);
    Announcement updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Long announcementId);
    Page<Video> getVideos(Integer status, Integer page, Integer size);
    Video createVideo(Video video);
    Video updateVideo(Video video);
    void deleteVideo(Long videoId);
    List<HelpArticle> getHelpArticles(String category);
    HelpArticle createHelpArticle(HelpArticle article);
    HelpArticle updateHelpArticle(HelpArticle article);
    void deleteHelpArticle(Long articleId);

    /**
     * Get all content categories.
     * Per D-CAT-03: flat categories only, no hierarchy.
     */
    List<ContentCategory> getCategories();

    /**
     * Create a new category.
     */
    ContentCategory createCategory(ContentCategory category);

    /**
     * Update an existing category.
     */
    ContentCategory updateCategory(ContentCategory category);

    /**
     * Delete a category (sets status to 0, does not physically delete).
     * Per D-CAT-04: shared categories between Video and HelpArticle.
     */
    void deleteCategory(Long categoryId);

    /**
     * Get content purchase records with pagination.
     * Per D-ANALYTICS-02: shows user, content title, price, payment method, time.
     */
    Page<ContentPurchaseRecordResponse> getPurchaseRecords(
        String contentType,
        String status,
        Long userId,
        Integer page,
        Integer size
    );

    /**
     * Get content purchase analytics.
     * Per D-ANALYTICS-01: total revenue, by type, top content, over time.
     */
    ContentAnalyticsResponse getAnalytics();
}
