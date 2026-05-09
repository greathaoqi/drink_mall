package com.drinkmall.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.*;
import com.drinkmall.mapper.*;
import com.drinkmall.service.admin.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private final BannerMapper bannerMapper;
    private final AnnouncementMapper announcementMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;

    @Override
    public List<Banner> getBanners(String location) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder);
        if (location != null) wrapper.eq(Banner::getLocation, location);
        return bannerMapper.selectList(wrapper);
    }

    @Override
    public Banner createBanner(Banner banner) {
        banner.setCreatedAt(LocalDateTime.now());
        bannerMapper.insert(banner);
        return banner;
    }

    @Override
    public Banner updateBanner(Banner banner) {
        banner.setUpdatedAt(LocalDateTime.now());
        bannerMapper.updateById(banner);
        return banner;
    }

    @Override
    public void deleteBanner(Long bannerId) {
        bannerMapper.deleteById(bannerId);
    }

    @Override
    public Page<Announcement> getAnnouncements(Integer status, Integer page, Integer size) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<Announcement>().orderByDesc(Announcement::getCreatedAt);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        return announcementMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    public Announcement updateAnnouncement(Announcement announcement) {
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateById(announcement);
        return announcement;
    }

    @Override
    public void deleteAnnouncement(Long announcementId) {
        announcementMapper.deleteById(announcementId);
    }

    @Override
    public Page<Video> getVideos(Integer status, Integer page, Integer size) {
        Page<Video> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<Video>().orderByDesc(Video::getCreatedAt);
        if (status != null) wrapper.eq(Video::getStatus, status);
        return videoMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Video createVideo(Video video) {
        video.setCreatedAt(LocalDateTime.now());
        videoMapper.insert(video);
        return video;
    }

    @Override
    public Video updateVideo(Video video) {
        video.setUpdatedAt(LocalDateTime.now());
        videoMapper.updateById(video);
        return video;
    }

    @Override
    public void deleteVideo(Long videoId) {
        videoMapper.deleteById(videoId);
    }

    @Override
    public List<HelpArticle> getHelpArticles(String category) {
        LambdaQueryWrapper<HelpArticle> wrapper = new LambdaQueryWrapper<HelpArticle>().orderByAsc(HelpArticle::getSortOrder);
        if (category != null) wrapper.eq(HelpArticle::getCategory, category);
        return helpArticleMapper.selectList(wrapper);
    }

    @Override
    public HelpArticle createHelpArticle(HelpArticle article) {
        article.setCreatedAt(LocalDateTime.now());
        helpArticleMapper.insert(article);
        return article;
    }

    @Override
    public HelpArticle updateHelpArticle(HelpArticle article) {
        article.setUpdatedAt(LocalDateTime.now());
        helpArticleMapper.updateById(article);
        return article;
    }

    @Override
    public void deleteHelpArticle(Long articleId) {
        helpArticleMapper.deleteById(articleId);
    }
}