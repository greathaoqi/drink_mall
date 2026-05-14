package com.drinkmall.dto;

import com.drinkmall.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamMemberResponse {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String memberLevelCode;

    public static TeamMemberResponse fromUser(User user) {
        return TeamMemberResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .memberLevelCode(user.getDistributionLevel())
                .build();
    }
}
