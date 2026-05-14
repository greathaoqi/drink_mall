package com.drinkmall.migration.importer.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MigrationImportValidationRequest {
    private String batchNo;
    private String sourceFileName;
    private String sourceFormat;
    private List<MigrationUserImportRow> users = new ArrayList<>();
    private List<MigrationAssetImportRow> assets = new ArrayList<>();
    private List<MigrationOrderImportRow> orders = new ArrayList<>();
    private List<MigrationProductImportRow> products = new ArrayList<>();
}
