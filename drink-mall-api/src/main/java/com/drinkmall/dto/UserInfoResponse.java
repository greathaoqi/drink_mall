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
    private String nickname;
    private String avatarUrl;
    private String phone;
    private BigDecimal balance;
    private Integer points;
    private Boolean ageVerified;

    public static UserInfoResponse fromEntity(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .openid(user.getOpenid())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .balance(user.getBalance())
                .points(user.getPoints())
                .ageVerified(user.getAgeVerified())
                .build();
    }
}
