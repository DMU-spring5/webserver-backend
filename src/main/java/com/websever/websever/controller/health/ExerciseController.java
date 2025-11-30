package com.websever.websever.controller.health;

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
     * 운동 카테고리 및 도구별 조회 API (PPT 49p ~ 55p)
     * GET /api/v1/health/exercise?category=가슴&tool=덤벨
     */
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tool
    ) {
        List<ExerciseResponse> responses = exerciseService.getExercisesByCategoryAndTool(category, tool);
        return ResponseEntity.ok(responses);
    }

    /**
     * 운동 상세 정보 조회 API (PPT 56p)
     * GET /api/v1/health/exercise/{exerciseId}
     */
    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponse> getExerciseDetail(@PathVariable Integer exerciseId) {
        ExerciseResponse response = exerciseService.getExerciseDetail(exerciseId);
        return ResponseEntity.ok(response);
    }

    /**
     * 운동 예상 소모 칼로리 조회 API (PPT 57p)
     * GET /api/v1/health/exercise/calculate?exerciseId=1&duration=30
     */
    @GetMapping("/calculate")
    public ResponseEntity<ExerciseCalculateResponse> calculateExerciseCalories(
            @RequestParam Integer exerciseId,
            @RequestParam Integer duration
    ) {
        ExerciseCalculateResponse response = exerciseService.calculateCalories(exerciseId, duration);
        return ResponseEntity.ok(response);
    }
}