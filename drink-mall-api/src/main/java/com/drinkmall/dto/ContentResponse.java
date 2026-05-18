package com.drinkmall.dto;

import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.Video;
import com.drinkmall.service.support.ContentAccessDecision;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ContentResponse {
    private Long id;
    private String title;
    private String type;
    private String coverUrl;
    private String videoUrl;
    private String content;
    private String preview;  // First 150 characters for teaser (unpurchased paid content)
    private String watchLevel;
    private String levelText;
    private Boolean canView;
    private Boolean canBuy;
    private Boolean paid;
    private Boolean purchased;
    private BigDecimal price;
    private String paymentMethods;
    private List<PaymentMethodResponse> payMethods;
    private List<PaymentMethodResponse> availablePayMethods;
    private String lockReason;
    private Integer likes;
    private Boolean userLiked;  // Whether current user has liked this content

    public static ContentResponse fromVideo(Video video, boolean purchased, BigDecimal price,
                                            ContentAccessDecision access,
                                            List<PaymentMethodResponse> payMethods) {
        // For videos, preview is the description/summary (video content is not text)
        // Videos typically don't have text content to preview, so preview remains null
        return ContentResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .type("video")
                .coverUrl(video.getCoverUrl())
                .videoUrl(access.isCanView() ? video.getVideoUrl() : null)
                .preview(null)  // Videos don't have text content for preview
                .watchLevel(access.getWatchLevel())
                .levelText(access.getLevelText())
                .canView(access.isCanView())
                .canBuy(access.isCanBuy())
                .paid(Boolean.TRUE.equals(video.getPaid()))
                .purchased(purchased)
                .price(price)
                .paymentMethods(video.getPaymentMethods())
                .payMethods(payMethods)
                .availablePayMethods(payMethods)
                .lockReason(access.getLockReason())
                .likes(video.getLikes() != null ? video.getLikes() : 0)
                .build();
    }

    public static ContentResponse fromArticle(HelpArticle article, boolean purchased, BigDecimal price,
                                              ContentAccessDecision access,
                                              List<PaymentMethodResponse> payMethods) {
        // Determine preview: first 150 chars of content for unpurchased paid content
        String preview = null;
        if (!access.isCanView() && Boolean.TRUE.equals(article.getPaid()) && article.getContent() != null) {
            preview = article.getContent().length() > 150
                    ? article.getContent().substring(0, 150) + "..."
                    : article.getContent();
        }

        return ContentResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .type("article")
                .content(access.isCanView() ? article.getContent() : null)
                .preview(preview)
                .watchLevel(access.getWatchLevel())
                .levelText(access.getLevelText())
                .canView(access.isCanView())
                .canBuy(access.isCanBuy())
                .paid(Boolean.TRUE.equals(article.getPaid()))
                .purchased(purchased)
                .price(price)
                .paymentMethods(article.getPaymentMethods())
                .payMethods(payMethods)
                .availablePayMethods(payMethods)
                .lockReason(access.getLockReason())
                .likes(article.getLikes() != null ? article.getLikes() : 0)
                .build();
    }
}
