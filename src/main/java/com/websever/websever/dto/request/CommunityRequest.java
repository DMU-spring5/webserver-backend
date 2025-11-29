package com.websever.websever.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommunityRequest {

    private String title;
    private String content;
}
