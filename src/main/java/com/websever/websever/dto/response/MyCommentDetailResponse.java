package com.websever.websever.dto.response;

import com.websever.websever.entity.community.CommentEntity;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class MyCommentDetailResponse {
    // 댓글 정보
    private Integer commentId;
    private String commentContent;
    private String commentedAt;

    // 원본 게시글 정보 (상세 내용 포함)
    private Integer postId;
    private String postTitle;
    private String postContent; // 리스트엔 없던 본문 추가
    private String postCreatedAt;
    private String postWriterNickname; // 원글 작성자

    public static MyCommentDetailResponse from(CommentEntity comment) {
        return MyCommentDetailResponse.builder()
                .commentId(comment.getCommentId())
                .commentContent(comment.getContent())
                .commentedAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .postId(comment.getPostId().getPost_id())
                .postTitle(comment.getPostId().getTitle())
                .postContent(comment.getPostId().getContent())
                .postCreatedAt(comment.getPostId().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                // 원글 작성자가 null일 경우 대비 (탈퇴 회원 등)
                .postWriterNickname(comment.getPostId().getUsers_id() != null ? comment.getPostId().getUsers_id().getNickname() : "알 수 없음")
                .build();
    }
}