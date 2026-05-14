package com.drinkmall.dto;

import com.drinkmall.entity.RealNameVerification;
import com.drinkmall.enums.RealNameStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RealNameStatusResponseTest {

    @Test
    void fromEntityMasksIdCardNumber() {
        RealNameVerification verification = new RealNameVerification();
        verification.setId(1L);
        verification.setStatus(RealNameStatus.REJECTED.getCode());
        verification.setIdCardNo("110101199001011234");
        verification.setRejectReason("照片不清晰");

        RealNameStatusResponse response = RealNameStatusResponse.fromEntity(verification);

        assertThat(response.getVerificationId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(RealNameStatus.REJECTED.getCode());
        assertThat(response.getMaskedIdCardNo()).isEqualTo("110101********1234");
        assertThat(response.getRejectReason()).isEqualTo("照片不清晰");
    }

    @Test
    void fromEntityHandlesNotSubmittedStatus() {
        RealNameVerification verification = new RealNameVerification();
        verification.setStatus(RealNameStatus.NOT_SUBMITTED.getCode());

        RealNameStatusResponse response = RealNameStatusResponse.fromEntity(verification);

        assertThat(response.getStatus()).isEqualTo(RealNameStatus.NOT_SUBMITTED.getCode());
        assertThat(response.getMaskedIdCardNo()).isNull();
    }
}
