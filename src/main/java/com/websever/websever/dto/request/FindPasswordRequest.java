package com.websever.websever.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPasswordRequest {
    private String nickname;
    private String userId;
}