package com.websever.websever.controller.health;

import com.websever.websever.dto.health.CalorieCalculatorDto;
import com.websever.websever.dto.health.HealthGoalResponse;
import com.websever.websever.dto.request.HealthRecordRequest;
import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.service.health.HealthService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/goal")
    public ResponseEntity<HealthGoalResponse> saveHealthGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CalorieCalculatorDto.Request request
    ) {
        String currentUserId = userDetails.getUsername();
        // 서비스 메서드 이름 변경 (calculatePlan -> saveGoal) 및 리턴 타입 통일
        HealthGoalResponse response = healthService.saveGoal(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * [신규] 사용자 목표 상세 정보 조회 API (PPT 12p)
     * GET /api/v1/health/goal
     */
    @GetMapping("/goal")
    public ResponseEntity<HealthGoalResponse> getHealthGoal(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String currentUserId = userDetails.getUsername();
        HealthGoalResponse response = healthService.getLatestGoal(currentUserId);
        return ResponseEntity.ok(response);
    }
}