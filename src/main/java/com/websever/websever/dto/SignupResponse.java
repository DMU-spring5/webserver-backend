package com.websever.websever.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private Integer id;
    private String userId;
    private String token;
}