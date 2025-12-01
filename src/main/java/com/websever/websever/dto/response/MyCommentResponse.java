package com.websever.websever.dto.response;

import com.websever.websever.entity.community.CommentEntity;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class MyCommentResponse {
    private Integer commentId;
    private String commentContent;  // 댓글 내용
    private String commentedAt;     // 댓글 작성일

    private Integer postId;         // 게시글 ID (이동용)
    private String postTitle;       // 게시글 제목

    public static MyCommentResponse from(CommentEntity comment) {
        return MyCommentResponse.builder()
                .commentId(comment.getCommentId())
                .commentContent(comment.getContent())
                .commentedAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .postId(comment.getPostId().getPost_id()) // postEntity의 getter 사용
                .postTitle(comment.getPostId().getTitle())
                .build();
    }
}