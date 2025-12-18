package com.websever.websever.dto.response;

import com.websever.websever.entity.community.postEntity;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class MyLikedPostResponse {
    private Integer postId;
    private String title;
    private String contentSummary;  // 내용 요약 (20자 이상 ...)
    private String writerNickname;  // 작성자 닉네임
    private String createdAt;       // 작성 날짜 (MM/dd 형식)
    private Integer likeCount;      // 좋아요 수

    public static MyLikedPostResponse of(postEntity post, Integer likeCount) {
        String content = post.getContent();
        if (content != null && content.length() > 20) {
            content = content.substring(0, 20) + "...";
        }

        return MyLikedPostResponse.builder()
                .postId(post.getPost_id())
                .title(post.getTitle())
                .contentSummary(content)
                // postEntity의 작성자 필드명(users_id) 사용
                .writerNickname(post.getUsers_id() != null ? post.getUsers_id().getNickname() : "알 수 없음")
                .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd")))
                .likeCount(likeCount)
                .build();
    }
}