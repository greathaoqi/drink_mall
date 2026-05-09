package com.drinkmall.controller.admin;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.Announcement;
import com.drinkmall.entity.Banner;
import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.Video;
import com.drinkmall.service.admin.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/content")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminContentController {

    private final AdminContentService adminContentService;

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners(@RequestParam(required = false) String location) {
        return Result.success(adminContentService.getBanners(location));
    }

    @PostMapping("/banners")
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        return Result.success(adminContentService.createBanner(banner));
    }

    @PutMapping("/banners/{bannerId}")
    public Result<Banner> updateBanner(@PathVariable Long bannerId, @RequestBody Banner banner) {
        banner.setId(bannerId);
        return Result.success(adminContentService.updateBanner(banner));
    }

    @DeleteMapping("/banners/{bannerId}")
    public Result<Void> deleteBanner(@PathVariable Long bannerId) {
        adminContentService.deleteBanner(bannerId);
        return Result.success(null);
    }

    @GetMapping("/announcements")
    public Result<Page<Announcement>> getAnnouncements(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminContentService.getAnnouncements(status, page, size));
    }

    @PostMapping("/announcements")
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        return Result.success(adminContentService.createAnnouncement(announcement));
    }

    @PutMapping("/announcements/{announcementId}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long announcementId, @RequestBody Announcement announcement) {
        announcement.setId(announcementId);
        return Result.success(adminContentService.updateAnnouncement(announcement));
    }

    @DeleteMapping("/announcements/{announcementId}")
    public Result<Void> deleteAnnouncement(@PathVariable Long announcementId) {
        adminContentService.deleteAnnouncement(announcementId);
        return Result.success(null);
    }

    @GetMapping("/videos")
    public Result<Page<Video>> getVideos(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return Result.success(adminContentService.getVideos(status, page, size));
    }

    @PostMapping("/videos")
    public Result<Video> createVideo(@RequestBody Video video) {
        return Result.success(adminContentService.createVideo(video));
    }

    @PutMapping("/videos/{videoId}")
    public Result<Video> updateVideo(@PathVariable Long videoId, @RequestBody Video video) {
        video.setId(videoId);
        return Result.success(adminContentService.updateVideo(video));
    }

    @DeleteMapping("/videos/{videoId}")
    public Result<Void> deleteVideo(@PathVariable Long videoId) {
        adminContentService.deleteVideo(videoId);
        return Result.success(null);
    }

    @GetMapping("/help-articles")
    public Result<List<HelpArticle>> getHelpArticles(@RequestParam(required = false) String category) {
        return Result.success(adminContentService.getHelpArticles(category));
    }

    @PostMapping("/help-articles")
    public Result<HelpArticle> createHelpArticle(@RequestBody HelpArticle article) {
        return Result.success(adminContentService.createHelpArticle(article));
    }

    @PutMapping("/help-articles/{articleId}")
    public Result<HelpArticle> updateHelpArticle(@PathVariable Long articleId, @RequestBody HelpArticle article) {
        article.setId(articleId);
        return Result.success(adminContentService.updateHelpArticle(article));
    }

    @DeleteMapping("/help-articles/{articleId}")
    public Result<Void> deleteHelpArticle(@PathVariable Long articleId) {
        adminContentService.deleteHelpArticle(articleId);
        return Result.success(null);
    }
}
