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
    private BigDecimal balance;
    private BigDecimal frozenBalance;
    private Integer points;
    private String distributionLevel;
    private BigDecimal teamPerformance;
    private BigDecimal dfBalance;
    private String inviteCode;
    private Long inviterId;
    private String registerSource;
    private String realNameStatus;
    private Boolean ageVerified;

    public static UserInfoResponse fromEntity(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .openid(user.getOpenid())
                .unionid(user.getUnionid())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .balance(user.getBalance())
                .frozenBalance(user.getFrozenBalance())
                .points(user.getPoints())
                .distributionLevel(user.getDistributionLevel())
                .teamPerformance(user.getTeamPerformance())
                .dfBalance(user.getDfBalance())
                .inviteCode(user.getInviteCode())
                .inviterId(user.getInviterId())
                .registerSource(user.getRegisterSource())
                .realNameStatus(user.getRealNameStatus())
                .ageVerified(user.getAgeVerified())
                .build();
    }
}
