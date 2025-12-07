package com.websever.websever.controller.health;

import com.websever.websever.dto.request.ExerciseCalculateRequest;
import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.dto.response.ExerciseResponse;
import com.websever.websever.service.health.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * [최종 수정] 운동 목록 조회 및 검색 API
     * 1. GET /api/v1/health/exercise?name=스쿼트 -> 검색 (name 파라미터만 허용)
     * 2. GET /api/v1/health/exercise -> 전체 조회 (name 파라미터 생략)
     */
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @RequestParam(required = false) String name // name 파라미터만 허용하도록 명시
    ) {
        // Service에는 name만 전달
        List<ExerciseResponse> responses = exerciseService.getExercises(name);
        return ResponseEntity.ok(responses);
    }

    /**
     * 운동 상세 정보 조회 API
     * GET /api/v1/health/exercise/{exerciseId}
     */
    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponse> getExerciseDetail(@PathVariable Integer exerciseId) {
        ExerciseResponse response = exerciseService.getExerciseDetail(exerciseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate/detail")
    public ResponseEntity<ExerciseCalculateResponse> calculateExerciseCaloriesDetail(
            @RequestBody ExerciseCalculateRequest request
    ) {
        ExerciseCalculateResponse response = exerciseService.calculateCaloriesDetail(request);
        return ResponseEntity.ok(response);
    }
}