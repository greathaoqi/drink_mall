package com.drinkmall.migration.importer.dto;

import lombok.Data;

@Data
public class MigrationUserImportRow {
    private String oldUserId;
    private String openid;
    private String unionid;
    private String nickname;
    private String phone;
    private String idCardNo;
    private String realName;
    private String inviterOldUserId;
    private String levelCode;
    private Boolean seedAccount;
}
