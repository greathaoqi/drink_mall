package com.drinkmall.dto;

import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.Video;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ContentResponse {
    private Long id;
    private String title;
    private String type;
    private String coverUrl;
    private String videoUrl;
    private String content;
    private String levelText;
    private Boolean canView;
    private Boolean paid;
    private BigDecimal price;

    public static ContentResponse fromVideo(Video video, boolean paid, BigDecimal price) {
        boolean canView = price.compareTo(BigDecimal.ZERO) == 0 || paid;
        return ContentResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .type("video")
                .coverUrl(video.getCoverUrl())
                .videoUrl(canView ? video.getVideoUrl() : null)
                .levelText("")
                .canView(canView)
                .paid(paid)
                .price(price)
                .build();
    }

    public static ContentResponse fromArticle(HelpArticle article, boolean paid, BigDecimal price) {
        boolean canView = price.compareTo(BigDecimal.ZERO) == 0 || paid;
        return ContentResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .type("article")
                .content(canView ? article.getContent() : null)
                .levelText("")
                .canView(canView)
                .paid(paid)
                .price(price)
                .build();
    }
}
