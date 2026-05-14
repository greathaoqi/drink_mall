package com.drinkmall.migration.importer.service;

import com.drinkmall.migration.importer.dto.MigrationAssetImportRow;
import com.drinkmall.migration.importer.dto.MigrationImportValidationRequest;
import com.drinkmall.migration.importer.dto.MigrationImportValidationReport;
import com.drinkmall.migration.importer.dto.MigrationUserImportRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MigrationImportValidationServiceTest {

    private MigrationImportValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new MigrationImportValidationServiceImpl();
    }

    @Test
    void validationReportsNormalUserWithoutInviter() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", null, "13800000001", "110101199001010011", "normal", false)
        ), List.of()));

        assertThat(report.getValid()).isFalse();
        assertThat(report.getIssues()).anySatisfy(issue -> {
            assertThat(issue.getCode()).isEqualTo("USER_INVITER_REQUIRED");
            assertThat(issue.getRowKey()).isEqualTo("U1");
        });
    }

    @Test
    void validationReportsDuplicatePhoneAndIdCard() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", null, "13800000001", "110101199001010011", "normal", true),
                user("U2", "U1", "13800000001", "110101199001010011", "promoter", false)
        ), List.of()));

        assertThat(report.getIssues()).anySatisfy(issue -> assertThat(issue.getCode()).isEqualTo("USER_DUPLICATE_PHONE"));
        assertThat(report.getIssues()).anySatisfy(issue -> assertThat(issue.getCode()).isEqualTo("USER_DUPLICATE_ID_CARD"));
    }

    @Test
    void validationReportsInvalidLevelAndNegativeAsset() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", null, "13800000001", "110101199001010011", "unknown_level", true)
        ), List.of(
                asset("U1", "balance", new BigDecimal("-0.01"))
        )));

        assertThat(report.getIssues()).anySatisfy(issue -> assertThat(issue.getCode()).isEqualTo("USER_INVALID_LEVEL"));
        assertThat(report.getIssues()).anySatisfy(issue -> assertThat(issue.getCode()).isEqualTo("ASSET_NEGATIVE_AMOUNT"));
    }

    @Test
    void validationReportsBrokenReferralChain() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", "MISSING", "13800000001", "110101199001010011", "normal", false)
        ), List.of()));

        assertThat(report.getIssues()).anySatisfy(issue -> {
            assertThat(issue.getCode()).isEqualTo("REFERRAL_CHAIN_BROKEN");
            assertThat(issue.getRowKey()).isEqualTo("U1");
        });
    }

    @Test
    void validationReportsReferralCycle() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", "U2", "13800000001", "110101199001010011", "normal", false),
                user("U2", "U1", "13800000002", "110101199001010012", "promoter", false)
        ), List.of()));

        assertThat(report.getIssues()).anySatisfy(issue -> assertThat(issue.getCode()).isEqualTo("REFERRAL_CHAIN_CYCLE"));
    }

    @Test
    void validSeedRootAndInvitedUserPass() {
        MigrationImportValidationReport report = validationService.validate(request(List.of(
                user("U1", null, "13800000001", "110101199001010011", "normal", true),
                user("U2", "U1", "13800000002", "110101199001010012", "promoter", false)
        ), List.of(
                asset("U1", "balance", new BigDecimal("0.00")),
                asset("U2", "df", new BigDecimal("12.34"))
        )));

        assertThat(report.getValid()).isTrue();
        assertThat(report.getIssues()).isEmpty();
        assertThat(report.getSummary().getUserRows()).isEqualTo(2);
        assertThat(report.getSummary().getAssetRows()).isEqualTo(2);
    }

    private MigrationImportValidationRequest request(List<MigrationUserImportRow> users, List<MigrationAssetImportRow> assets) {
        MigrationImportValidationRequest request = new MigrationImportValidationRequest();
        request.setBatchNo("BATCH-TEST");
        request.setUsers(users);
        request.setAssets(assets);
        return request;
    }

    private MigrationUserImportRow user(String oldUserId, String inviterOldUserId, String phone, String idCardNo,
                                        String levelCode, boolean seedAccount) {
        MigrationUserImportRow row = new MigrationUserImportRow();
        row.setOldUserId(oldUserId);
        row.setInviterOldUserId(inviterOldUserId);
        row.setPhone(phone);
        row.setIdCardNo(idCardNo);
        row.setLevelCode(levelCode);
        row.setSeedAccount(seedAccount);
        return row;
    }

    private MigrationAssetImportRow asset(String oldUserId, String assetType, BigDecimal availableAmount) {
        MigrationAssetImportRow row = new MigrationAssetImportRow();
        row.setOldUserId(oldUserId);
        row.setAssetType(assetType);
        row.setAvailableAmount(availableAmount);
        return row;
    }
}
