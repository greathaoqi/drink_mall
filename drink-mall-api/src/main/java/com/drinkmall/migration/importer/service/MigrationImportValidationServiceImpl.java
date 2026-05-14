package com.drinkmall.migration.importer.service;

import com.drinkmall.enums.AssetType;
import com.drinkmall.enums.UserLevel;
import com.drinkmall.migration.importer.dto.MigrationAssetImportRow;
import com.drinkmall.migration.importer.dto.MigrationImportValidationIssue;
import com.drinkmall.migration.importer.dto.MigrationImportValidationReport;
import com.drinkmall.migration.importer.dto.MigrationImportValidationRequest;
import com.drinkmall.migration.importer.dto.MigrationImportValidationSummary;
import com.drinkmall.migration.importer.dto.MigrationUserImportRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MigrationImportValidationServiceImpl implements MigrationImportValidationService {

    private static final String SEVERITY_ERROR = "error";
    private static final Set<String> SUPPORTED_LEVEL_CODES = EnumSet.allOf(UserLevel.class).stream()
            .map(UserLevel::getCode)
            .collect(Collectors.toUnmodifiableSet());
    private static final Set<String> SUPPORTED_ASSET_TYPES = EnumSet.allOf(AssetType.class).stream()
            .map(AssetType::getCode)
            .collect(Collectors.toUnmodifiableSet());

    @Override
    public MigrationImportValidationReport validate(MigrationImportValidationRequest request) {
        MigrationImportValidationRequest safeRequest = request == null ? new MigrationImportValidationRequest() : request;
        List<MigrationUserImportRow> users = safeList(safeRequest.getUsers());
        List<MigrationAssetImportRow> assets = safeList(safeRequest.getAssets());
        List<MigrationImportValidationIssue> issues = new ArrayList<>();

        validateUsers(users, issues);
        validateAssets(assets, issues);

        MigrationImportValidationSummary summary = MigrationImportValidationSummary.builder()
                .userRows(users.size())
                .assetRows(assets.size())
                .orderRows(safeList(safeRequest.getOrders()).size())
                .productRows(safeList(safeRequest.getProducts()).size())
                .errorCount(issues.size())
                .build();

        return MigrationImportValidationReport.builder()
                .batchNo(safeRequest.getBatchNo())
                .valid(issues.isEmpty())
                .validatedAt(LocalDateTime.now())
                .summary(summary)
                .issues(issues)
                .build();
    }

    private void validateUsers(List<MigrationUserImportRow> users, List<MigrationImportValidationIssue> issues) {
        Map<String, MigrationUserImportRow> usersByOldId = users.stream()
                .filter(row -> hasText(row.getOldUserId()))
                .collect(Collectors.toMap(MigrationUserImportRow::getOldUserId, Function.identity(), (left, right) -> left));

        Map<String, List<MigrationUserImportRow>> byPhone = groupByNonBlank(users, MigrationUserImportRow::getPhone);
        byPhone.forEach((phone, rows) -> {
            if (rows.size() > 1) {
                rows.forEach(row -> addIssue(issues, "USER_DUPLICATE_PHONE", "users", row.getOldUserId(), "phone",
                        "手机号在导入文件中重复: " + phone));
            }
        });

        Map<String, List<MigrationUserImportRow>> byIdCard = groupByNonBlank(users, MigrationUserImportRow::getIdCardNo);
        byIdCard.forEach((idCardNo, rows) -> {
            if (rows.size() > 1) {
                rows.forEach(row -> addIssue(issues, "USER_DUPLICATE_ID_CARD", "users", row.getOldUserId(), "idCardNo",
                        "身份证号在导入文件中重复: " + idCardNo));
            }
        });

        for (MigrationUserImportRow row : users) {
            if (!Boolean.TRUE.equals(row.getSeedAccount()) && !hasText(row.getInviterOldUserId())) {
                addIssue(issues, "USER_INVITER_REQUIRED", "users", row.getOldUserId(), "inviterOldUserId",
                        "普通用户必须绑定上级，只有后台种子账号允许无上级");
            }
            if (!isSupportedLevel(row.getLevelCode())) {
                addIssue(issues, "USER_INVALID_LEVEL", "users", row.getOldUserId(), "levelCode",
                        "等级编码不在系统 UserLevel 枚举中");
            }
            if (hasText(row.getInviterOldUserId()) && !usersByOldId.containsKey(row.getInviterOldUserId())) {
                addIssue(issues, "REFERRAL_CHAIN_BROKEN", "users", row.getOldUserId(), "inviterOldUserId",
                        "推荐链断裂，上级旧用户ID不存在: " + row.getInviterOldUserId());
            }
        }

        detectReferralCycles(usersByOldId, issues);
    }

    private void validateAssets(List<MigrationAssetImportRow> assets, List<MigrationImportValidationIssue> issues) {
        for (MigrationAssetImportRow row : assets) {
            if (row.getAvailableAmount() != null && row.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0) {
                addIssue(issues, "ASSET_NEGATIVE_AMOUNT", "assets", row.getOldUserId(), "availableAmount",
                        "资产可用余额不能为负数");
            }
            if (row.getFrozenAmount() != null && row.getFrozenAmount().compareTo(BigDecimal.ZERO) < 0) {
                addIssue(issues, "ASSET_NEGATIVE_AMOUNT", "assets", row.getOldUserId(), "frozenAmount",
                        "资产冻结余额不能为负数");
            }
            if (hasText(row.getAssetType()) && !SUPPORTED_ASSET_TYPES.contains(row.getAssetType().toLowerCase(Locale.ROOT))) {
                addIssue(issues, "ASSET_INVALID_TYPE", "assets", row.getOldUserId(), "assetType",
                        "资产类型不在系统 AssetType 枚举中");
            }
        }
    }

    private void detectReferralCycles(Map<String, MigrationUserImportRow> usersByOldId, List<MigrationImportValidationIssue> issues) {
        Set<String> cycleReported = new HashSet<>();
        for (String oldUserId : usersByOldId.keySet()) {
            Set<String> path = new HashSet<>();
            String current = oldUserId;
            while (hasText(current) && usersByOldId.containsKey(current)) {
                if (!path.add(current)) {
                    if (cycleReported.add(current)) {
                        addIssue(issues, "REFERRAL_CHAIN_CYCLE", "users", oldUserId, "inviterOldUserId",
                                "推荐链存在循环，需人工修正后再导入");
                    }
                    break;
                }
                current = usersByOldId.get(current).getInviterOldUserId();
            }
        }
    }

    private boolean isSupportedLevel(String levelCode) {
        return hasText(levelCode) && SUPPORTED_LEVEL_CODES.contains(levelCode.toLowerCase(Locale.ROOT));
    }

    private <T> List<T> safeList(List<T> rows) {
        return rows == null ? List.of() : rows.stream().filter(Objects::nonNull).toList();
    }

    private <T> Map<String, List<T>> groupByNonBlank(List<T> rows, Function<T, String> keyExtractor) {
        Map<String, List<T>> grouped = new HashMap<>();
        for (T row : rows) {
            String key = keyExtractor.apply(row);
            if (hasText(key)) {
                grouped.computeIfAbsent(key.trim(), ignored -> new ArrayList<>()).add(row);
            }
        }
        return grouped;
    }

    private void addIssue(List<MigrationImportValidationIssue> issues, String code, String sheetName, String rowKey,
                          String fieldName, String message) {
        issues.add(MigrationImportValidationIssue.builder()
                .code(code)
                .severity(SEVERITY_ERROR)
                .sheetName(sheetName)
                .rowKey(rowKey)
                .fieldName(fieldName)
                .message(message)
                .build());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
