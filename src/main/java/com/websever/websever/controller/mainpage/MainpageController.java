package com.websever.websever.controller.mainpage;

import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.service.auth.CustomUserDetailsService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mainpage")
@RequiredArgsConstructor // 생성자 코드를 대체
public class MainpageController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String currentUserId = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUserId);

        return ResponseEntity.ok(userInfo);
    }
}
