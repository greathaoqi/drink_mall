package com.drinkmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.Result;
import com.drinkmall.entity.*;
import com.drinkmall.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final AnnouncementMapper announcementMapper;
    private final VideoMapper videoMapper;
    private final HelpArticleMapper helpArticleMapper;

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
}
