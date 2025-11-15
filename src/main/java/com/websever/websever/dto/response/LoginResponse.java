package com.websever.websever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}