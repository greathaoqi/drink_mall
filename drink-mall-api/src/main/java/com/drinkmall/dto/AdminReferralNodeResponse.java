package com.drinkmall.dto;

import com.drinkmall.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReferralNodeResponse {
    private Long userId;
    private Long inviterId;
    private String nickname;
    private String avatarUrl;
    private String memberLevelCode;
    private Integer depth;

    public static AdminReferralNodeResponse fromUser(User user, Integer depth) {
        return AdminReferralNodeResponse.builder()
                .userId(user.getId())
                .inviterId(user.getInviterId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .memberLevelCode(user.getDistributionLevel())
                .depth(depth)
                .build();
    }
}
