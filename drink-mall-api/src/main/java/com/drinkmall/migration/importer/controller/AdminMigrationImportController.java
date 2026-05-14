package com.drinkmall.migration.importer.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.drinkmall.common.Result;
import com.drinkmall.migration.importer.dto.MigrationImportReportExportResponse;
import com.drinkmall.migration.importer.dto.MigrationImportValidationReport;
import com.drinkmall.migration.importer.dto.MigrationImportValidationRequest;
import com.drinkmall.migration.importer.service.MigrationImportValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/migration/import")
@RequiredArgsConstructor
@SaCheckRole("admin")
public class AdminMigrationImportController {

    private final MigrationImportValidationService validationService;

    @PostMapping("/validate")
    public Result<MigrationImportValidationReport> validate(@RequestBody MigrationImportValidationRequest request) {
        return Result.success(validationService.validate(request));
    }

    @GetMapping("/reports/{reportId}/export")
    public Result<MigrationImportReportExportResponse> exportReport(@PathVariable String reportId) {
        return Result.success(MigrationImportReportExportResponse.builder()
                .reportId(reportId)
                .status("reserved")
                .expectedFormat("xlsx")
                .message("一期仅预留异常报告导出接口，待接入文件存储和真实导入批次后生成 Excel")
                .build());
    }
}
