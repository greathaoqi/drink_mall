package com.drinkmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.drinkmall.common.BusinessException;
import com.drinkmall.dto.RealNameSubmitRequest;
import com.drinkmall.entity.OperationLog;
import com.drinkmall.entity.RealNameVerification;
import com.drinkmall.entity.User;
import com.drinkmall.enums.RealNameStatus;
import com.drinkmall.mapper.OperationLogMapper;
import com.drinkmall.mapper.RealNameVerificationMapper;
import com.drinkmall.mapper.UserMapper;
import com.drinkmall.service.RealNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RealNameServiceImpl implements RealNameService {

    private final RealNameVerificationMapper realNameVerificationMapper;
    private final UserMapper userMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    @Transactional
    public RealNameVerification submit(Long userId, RealNameSubmitRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (RealNameStatus.APPROVED.getCode().equals(user.getRealNameStatus())) {
            throw new BusinessException(400, "实名认证已通过，请勿重复提交");
        }
        Long duplicated = realNameVerificationMapper.selectCount(
                new LambdaQueryWrapper<RealNameVerification>()
                        .eq(RealNameVerification::getIdCardNo, request.getIdCardNo())
                        .ne(RealNameVerification::getUserId, userId)
                        .eq(RealNameVerification::getStatus, RealNameStatus.APPROVED.getCode())
        );
        if (duplicated != null && duplicated > 0) {
            throw new BusinessException(400, "身份证号已被实名使用");
        }

        RealNameVerification verification = new RealNameVerification();
        verification.setUserId(userId);
        verification.setRealName(request.getRealName());
        verification.setIdCardNo(request.getIdCardNo());
        verification.setFrontImageUrl(request.getFrontImageUrl());
        verification.setBackImageUrl(request.getBackImageUrl());
        verification.setStatus(RealNameStatus.PENDING.getCode());
        verification.setCreatedAt(LocalDateTime.now());
        verification.setUpdatedAt(LocalDateTime.now());
        realNameVerificationMapper.insert(verification);

        user.setRealNameStatus(RealNameStatus.PENDING.getCode());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return verification;
    }

    @Override
    public RealNameVerification getStatus(Long userId) {
        RealNameVerification latest = realNameVerificationMapper.selectOne(
                new LambdaQueryWrapper<RealNameVerification>()
                        .eq(RealNameVerification::getUserId, userId)
                        .orderByDesc(RealNameVerification::getCreatedAt)
                        .last("LIMIT 1")
        );
        if (latest != null) {
            return latest;
        }
        RealNameVerification empty = new RealNameVerification();
        empty.setUserId(userId);
        empty.setStatus(RealNameStatus.NOT_SUBMITTED.getCode());
        return empty;
    }

    @Override
    public Page<RealNameVerification> getReviewPage(String status, String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<RealNameVerification> wrapper = new LambdaQueryWrapper<RealNameVerification>()
                .orderByDesc(RealNameVerification::getCreatedAt);
        if (status != null && !status.isBlank()) {
            wrapper.eq(RealNameVerification::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(RealNameVerification::getRealName, keyword)
                    .or()
                    .like(RealNameVerification::getIdCardNo, keyword));
        }
        return realNameVerificationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public void review(Long verificationId, Long adminId, boolean approved, String rejectReason) {
        RealNameVerification verification = realNameVerificationMapper.selectById(verificationId);
        if (verification == null) {
            throw new BusinessException(404, "实名申请不存在");
        }
        if (!RealNameStatus.PENDING.getCode().equals(verification.getStatus())) {
            throw new BusinessException(400, "实名申请已处理，不能重复审核");
        }
        if (approved) {
            Long duplicated = realNameVerificationMapper.selectCount(
                    new LambdaQueryWrapper<RealNameVerification>()
                            .eq(RealNameVerification::getIdCardNo, verification.getIdCardNo())
                            .ne(RealNameVerification::getUserId, verification.getUserId())
                            .eq(RealNameVerification::getStatus, RealNameStatus.APPROVED.getCode())
            );
            if (duplicated != null && duplicated > 0) {
                throw new BusinessException(400, "身份证号已被其他账号审核通过");
            }
        } else if (rejectReason == null || rejectReason.isBlank()) {
            throw new BusinessException(400, "驳回原因不能为空");
        }

        String status = approved ? RealNameStatus.APPROVED.getCode() : RealNameStatus.REJECTED.getCode();
        verification.setStatus(status);
        verification.setRejectReason(approved ? null : rejectReason);
        verification.setReviewerAdminId(adminId);
        verification.setReviewedAt(LocalDateTime.now());
        verification.setUpdatedAt(LocalDateTime.now());
        realNameVerificationMapper.updateById(verification);

        User user = userMapper.selectById(verification.getUserId());
        if (user != null) {
            user.setRealNameStatus(status);
            user.setAgeVerified(approved);
            user.setAgeVerifiedAt(approved ? LocalDateTime.now() : user.getAgeVerifiedAt());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }

        OperationLog log = new OperationLog();
        log.setAdminUserId(adminId);
        log.setModule("real_name");
        log.setAction(approved ? "approve" : "reject");
        log.setTargetId(String.valueOf(verificationId));
        log.setDetail("status=" + status + ", reason=" + (rejectReason == null ? "" : rejectReason));
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
