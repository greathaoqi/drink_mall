package com.drinkmall.dto;

import com.drinkmall.entity.HelpArticle;
import com.drinkmall.entity.Video;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ContentManagementContractTest {

    @Test
    void videoCarriesPhaseOneWatchAndPayFields() {
        Video video = new Video();
        video.setWatchLevel("promoter");
        video.setPaid(true);
        video.setPrice(new BigDecimal("9.90"));
        video.setPaymentMethods("balance,df");
        video.setLikes(12);

        assertThat(video.getWatchLevel()).isEqualTo("promoter");
        assertThat(video.getPaid()).isTrue();
        assertThat(video.getPrice()).isEqualByComparingTo("9.90");
        assertThat(video.getPaymentMethods()).isEqualTo("balance,df");
        assertThat(video.getLikes()).isEqualTo(12);
    }

    @Test
    void helpArticleCarriesPhaseOneWatchAndPayFields() {
        HelpArticle article = new HelpArticle();
        article.setWatchLevel("normal");
        article.setPaid(false);
        article.setPrice(BigDecimal.ZERO);
        article.setPaymentMethods("balance");
        article.setLikes(3);

        assertThat(article.getWatchLevel()).isEqualTo("normal");
        assertThat(article.getPaid()).isFalse();
        assertThat(article.getPrice()).isEqualByComparingTo("0");
        assertThat(article.getPaymentMethods()).isEqualTo("balance");
        assertThat(article.getLikes()).isEqualTo(3);
    }
}
