package com.websever.websever.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token; // JWT 토큰을 담을 필드

}
