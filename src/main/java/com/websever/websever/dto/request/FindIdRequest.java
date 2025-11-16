package com.websever.websever.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindIdRequest {
    private String nickname;
    private String password;
}