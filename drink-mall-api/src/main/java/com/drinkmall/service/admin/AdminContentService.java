package com.drinkmall.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
}
