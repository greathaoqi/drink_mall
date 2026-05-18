package com.drinkmall.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.drinkmall.common.Result;
import com.drinkmall.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/image")
    @SaCheckLogin
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();
        return Result.success(Map.of("url", imageUploadService.store(file)));
    }
}
