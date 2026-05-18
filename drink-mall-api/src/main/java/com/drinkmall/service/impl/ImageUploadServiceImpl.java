package com.drinkmall.service.impl;

import com.drinkmall.common.BusinessException;
import com.drinkmall.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final Path uploadBaseDir;

    public ImageUploadServiceImpl(@Value("${app.upload.base-dir:uploads}") String uploadBaseDir) {
        this.uploadBaseDir = Path.of(uploadBaseDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请上传图片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片大小不能超过5MB");
        }
        String extension = extension(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(extension) || contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException(400, "仅支持 jpg、png、webp 图片");
        }

        String datePath = LocalDate.now().toString().replace("-", "/");
        Path relativeDir = Path.of("real-name", datePath);
        String filename = UUID.randomUUID() + "." + extension;
        Path target = uploadBaseDir.resolve(relativeDir).resolve(filename).normalize();
        if (!target.startsWith(uploadBaseDir)) {
            throw new BusinessException(400, "上传路径非法");
        }

        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
            return "/uploads/" + relativeDir.toString().replace('\\', '/') + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException(500, "图片上传失败");
        }
    }

    private String extension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
