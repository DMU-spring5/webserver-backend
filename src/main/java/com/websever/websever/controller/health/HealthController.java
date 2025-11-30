package com.websever.websever.controller.health;

import com.websever.websever.dto.request.HealthRecordRequest;
import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.service.health.HealthService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;
    private final UserService userService;

    /**
     * 칼로리 계산기 결과 저장 및 조회 (POST)
     * 단순 조회가 아니라 '내 기록'으로 저장합니다.
     */
    @PostMapping("/record")
    public ResponseEntity<ExerciseCalculateResponse> recordHealth(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody HealthRecordRequest request
    ) {
        // 로그인한 사용자 정보 가져오기
        String currentUserId = userDetails.getUsername();

        // 서비스 호출 (저장 및 계산 결과 반환)
        ExerciseCalculateResponse response = healthService.recordExercise(currentUserId, request);

        return ResponseEntity.ok(response);
    }
}