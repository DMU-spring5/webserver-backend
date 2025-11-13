package com.websever.websever.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordRequest {
    private String nickname;
    private String userId;
}