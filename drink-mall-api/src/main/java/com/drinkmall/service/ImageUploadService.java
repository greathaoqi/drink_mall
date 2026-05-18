package com.drinkmall.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String store(MultipartFile file);
}
