package com.websever.websever.controller.mainpage;

import com.websever.websever.dto.MilitaryDto;
import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.entity.auth.ServiceType;
import com.websever.websever.service.auth.CustomUserDetailsService;
import com.websever.websever.service.mainpage.MilitaryService;
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
@RequiredArgsConstructor
public class MainpageController {

    private final UserService userService;
    private final MilitaryService  militaryService;

    @GetMapping
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        ServiceType branchType = userInfo.getServiceType();

        MilitaryDto progressDto = militaryService.calculateProgress(currentUsername, branchType);

        userInfo.setMilitaryProgress(progressDto);

        return ResponseEntity.ok(userInfo);
    }
}
