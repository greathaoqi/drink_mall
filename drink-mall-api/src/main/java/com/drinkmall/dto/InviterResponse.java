package com.drinkmall.dto;

import com.drinkmall.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InviterResponse {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String inviteCode;

    public static InviterResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        return InviterResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .inviteCode(user.getInviteCode())
                .build();
    }
}
