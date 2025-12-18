package com.websever.websever.dto.response;

import com.websever.websever.entity.community.postEntity;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class MyLikedPostDetailResponse {
    private Integer postId;
    private String title;
    private String content;         // 전체 본문
    private String writerNickname;  // 작성자
    private String createdAt;       // 작성일
    private Integer likeCount;      // 좋아요 수

    public static MyLikedPostDetailResponse of(postEntity post, Integer likeCount) {
        return MyLikedPostDetailResponse.builder()
                .postId(post.getPost_id())
                .title(post.getTitle())
                .content(post.getContent())
                .writerNickname(post.getUsers_id() != null ? post.getUsers_id().getNickname() : "알 수 없음")
                .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .likeCount(likeCount)
                .build();
    }
}