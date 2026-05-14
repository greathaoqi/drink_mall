package com.drinkmall.dto;

import com.drinkmall.entity.RealNameVerification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RealNameStatusResponse {
    private Long verificationId;
    private String status;
    private String maskedIdCardNo;
    private String rejectReason;

    public static RealNameStatusResponse fromEntity(RealNameVerification verification) {
        return RealNameStatusResponse.builder()
                .verificationId(verification.getId())
                .status(verification.getStatus())
                .maskedIdCardNo(maskIdCardNo(verification.getIdCardNo()))
                .rejectReason(verification.getRejectReason())
                .build();
    }

    private static String maskIdCardNo(String idCardNo) {
        if (idCardNo == null || idCardNo.length() < 8) {
            return null;
        }
        return idCardNo.substring(0, 6) + "********" + idCardNo.substring(idCardNo.length() - 4);
    }
}
