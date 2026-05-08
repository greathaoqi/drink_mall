package com.drinkmall.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private Boolean ageVerified;
    private Boolean isNewUser;
}
