package com.websever.websever.controller.community;

import com.websever.websever.dto.request.CommunityRequest;
import com.websever.websever.dto.response.CommunityResponse;
import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.service.Community.CommunityService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CommunityResponse> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CommunityRequest request) {

        String currentUsername = userDetails.getUsername();

        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        return ResponseEntity.ok(
                communityService.create(userInfo.getUserId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getAllPosts() {
        List<CommunityResponse> response = communityService.getAllPosts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommunityResponse> getPostById(@PathVariable Integer postId) {
        CommunityResponse response = communityService.getPostById(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<CommunityResponse> updatePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer postId,
            @RequestBody CommunityRequest request) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        CommunityResponse response = communityService.updatePost(postId, currentUsername, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer postId) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        communityService.deletePost(postId, currentUsername);
        return ResponseEntity.noContent().build();
    }

}
