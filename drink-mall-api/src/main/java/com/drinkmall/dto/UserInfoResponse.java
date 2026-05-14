package com.drinkmall.dto;

import com.drinkmall.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserInfoResponse {
    private Long userId;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String mobile;
    private BigDecimal balance;
    private BigDecimal frozenBalance;
    private Integer points;
    private String distributionLevel;
    private String levelName;
    private BigDecimal teamPerformance;
    private BigDecimal dfBalance;
    private BigDecimal df;
    private BigDecimal wineBean;
    private BigDecimal optionValue;
    private String inviteCode;
    private Long inviterId;
    private String inviterName;
    private String registerSource;
    private String realNameStatus;
    private Boolean showOption;
    private Boolean ageVerified;

    public static UserInfoResponse fromEntity(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .openid(user.getOpenid())
                .unionid(user.getUnionid())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .mobile(user.getPhone())
                .balance(user.getBalance())
                .frozenBalance(user.getFrozenBalance())
                .points(user.getPoints())
                .distributionLevel(user.getDistributionLevel())
                .levelName(levelName(user.getDistributionLevel()))
                .teamPerformance(user.getTeamPerformance())
                .dfBalance(user.getDfBalance())
                .df(user.getDfBalance())
                .wineBean(user.getWineBeanBalance())
                .optionValue(user.getOptionBalance())
                .inviteCode(user.getInviteCode())
                .inviterId(user.getInviterId())
                .registerSource(user.getRegisterSource())
                .realNameStatus(user.getRealNameStatus())
                .showOption(false)
                .ageVerified(user.getAgeVerified())
                .build();
    }

    private static String levelName(String levelCode) {
        if ("promoter".equals(levelCode)) {
            return "推客";
        }
        if ("county".equals(levelCode)) {
            return "县级联营商";
        }
        if ("city".equals(levelCode)) {
            return "市级联营商";
        }
        if ("province".equals(levelCode)) {
            return "省级联营商";
        }
        return "普通用户";
    }
}
