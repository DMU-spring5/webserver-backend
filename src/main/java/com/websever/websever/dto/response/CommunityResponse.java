package com.websever.websever.dto.response;

import com.websever.websever.entity.community.postEntity;
import lombok.Data;

@Data
public class CommunityResponse {
    private Integer post_id;
    private Integer user_id;
    private String title;
    private String content;

    public static CommunityResponse create(postEntity entity) {
        CommunityResponse response = new CommunityResponse();

        response.setPost_id(entity.getPost_id());
        response.setUser_id(entity.getUsers_id().getId());

        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());

        return response;
    }
}
