package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageUploadServiceImplTest {

    @TempDir
    Path tempDir;

    @Test
    void storesImageAndReturnsUploadsUrl() throws Exception {
        ImageUploadServiceImpl service = new ImageUploadServiceImpl(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "id-front.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        String url = service.store(file);

        assertThat(url).startsWith("/uploads/real-name/");
        Path storedPath = tempDir.resolve(url.replaceFirst("^/uploads/", "").replace("/", java.io.File.separator));
        assertThat(Files.exists(storedPath)).isTrue();
    }

    @Test
    void rejectsNonImageFiles() {
        ImageUploadServiceImpl service = new ImageUploadServiceImpl(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "id-front.txt",
                "text/plain",
                new byte[]{1, 2, 3}
        );

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("图片");
    }
}
