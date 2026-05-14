package com.drinkmall.service.admin.impl;

import com.drinkmall.entity.Banner;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.mapper.AnnouncementMapper;
import com.drinkmall.mapper.BannerMapper;
import com.drinkmall.mapper.HelpArticleMapper;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.VideoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminContentServiceImplTest {

    @Mock private BannerMapper bannerMapper;
    @Mock private AnnouncementMapper announcementMapper;
    @Mock private VideoMapper videoMapper;
    @Mock private HelpArticleMapper helpArticleMapper;
    @Mock private OperationLogMapper operationLogMapper;

    private AdminContentServiceImpl adminContentService;

    @BeforeEach
    void setUp() {
        adminContentService = new AdminContentServiceImpl(
                bannerMapper,
                announcementMapper,
                videoMapper,
                helpArticleMapper,
                operationLogMapper
        );
    }

    @Test
    void createBannerWritesAdminContentOperationLogWithObjectAndSource() {
        Banner banner = new Banner();
        banner.setId(10L);
        banner.setTitle("首页主图");
        banner.setImageUrl("https://example.com/banner.png");
        banner.setStatus(1);

        adminContentService.createBanner(banner);

        ArgumentCaptor<OperationLog> log = ArgumentCaptor.forClass(OperationLog.class);
        verify(operationLogMapper).insert(log.capture());
        assertThat(log.getValue().getModule()).isEqualTo("banner");
        assertThat(log.getValue().getAction()).isEqualTo("create");
        assertThat(log.getValue().getTargetId()).isEqualTo("10");
        assertThat(log.getValue().getDetail()).contains("after=");
        assertThat(log.getValue().getDetail()).contains("source=admin_content");
        assertThat(log.getValue().getCreatedAt()).isNotNull();
    }
}
