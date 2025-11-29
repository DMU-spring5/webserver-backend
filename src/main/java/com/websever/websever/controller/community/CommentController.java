package com.websever.websever.controller.community;

import com.websever.websever.dto.request.CommentRequest;
import com.websever.websever.dto.response.CommentResponse;
import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.service.Community.CommentService;
import com.websever.websever.service.Community.CommunityService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommunityService communityService;
    private final UserService userService;
    private final CommentService commentService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer postId,
            @RequestBody CommentRequest request) {

        String currentUsername = userDetails.getUsername();

        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        return ResponseEntity.ok(
                commentService.create(userInfo.getUserId(), postId, request)
        );
    }

    @GetMapping("/posts/{postId}/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable Integer postId,
            @PathVariable Integer commentId) {

        CommentResponse response = commentService.getCommentById(postId, commentId);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer commentId) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        commentService.deleteComment(commentId, userInfo.getUserId());

        return ResponseEntity.noContent().build();
    }
}
