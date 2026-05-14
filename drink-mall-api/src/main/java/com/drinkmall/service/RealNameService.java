package com.drinkmall.service;

import com.drinkmall.dto.RealNameSubmitRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.entity.RealNameVerification;

public interface RealNameService {
    RealNameVerification submit(Long userId, RealNameSubmitRequest request);

    RealNameVerification getStatus(Long userId);

    Page<RealNameVerification> getReviewPage(String status, String keyword, Integer page, Integer size);

    void review(Long verificationId, Long adminId, boolean approved, String rejectReason);
}
