package com.websever.websever.dto.response;

import com.websever.websever.entity.community.CommentEntity;
import lombok.Data;

@Data
public class CommentResponse {
    private Integer commentId;
    private String postTitle;
    private String postContent;
    private String comment;
    private Integer usersId;
    private Integer postId;

    public static CommentResponse create(CommentEntity entity) {
        CommentResponse response = new CommentResponse();

        response.setCommentId(entity.getCommentId());
        response.setPostTitle(String.valueOf(entity.getPostTitle()));
        response.setPostContent(String.valueOf(entity.getPostContent()));
        response.setComment(entity.getContent());
        response.setUsersId(entity.getUsersId().getId());
        response.setPostId(entity.getPostId().getPost_id());

        return response;
    }
}
