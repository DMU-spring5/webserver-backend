package com.websever.websever.controller.mypage;

import com.websever.websever.dto.request.ChangePasswordRequest;
import com.websever.websever.dto.response.MyCommentDetailResponse;
import com.websever.websever.dto.response.MyCommentResponse;
import com.websever.websever.dto.response.MyLikedPostResponse;
import com.websever.websever.service.mypage.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 비밀번호 변경 API
     * POST /api/v1/mypage/password/change
     */
    @PostMapping("/password/change")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest request
    ) {
        myPageService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    /**
     * 내가 쓴 댓글 목록 조회 API (댓글 단 글 리스트)
     * GET /api/v1/mypage/comments
     */
    @GetMapping("/comments")
    public ResponseEntity<List<MyCommentResponse>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MyCommentResponse> responses = myPageService.getMyComments(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<MyCommentDetailResponse> getMyCommentDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer commentId
    ) {
        MyCommentDetailResponse response = myPageService.getMyCommentDetail(userDetails.getUsername(), commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes")
    public ResponseEntity<List<MyLikedPostResponse>> getMyLikedPosts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<MyLikedPostResponse> responses = myPageService.getMyLikedPosts(userDetails.getUsername());
        return ResponseEntity.ok(responses);
    }
}