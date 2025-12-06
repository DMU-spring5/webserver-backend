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
     * 운동 카테고리 및 도구별 조회 API (PPT 49p ~ 55p)
     * GET /api/v1/health/exercise?category=가슴&tool=덤벨
     */
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tool,
            @RequestParam(required = false) String name // [신규] 검색어 파라미터 추가
    ) {
        // 서비스 메서드 시그니처 변경: name 파라미터 전달
        List<ExerciseResponse> responses = exerciseService.getExercises(category, tool, name);
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

    @PostMapping("/calculate/detail")
    public ResponseEntity<ExerciseCalculateResponse> calculateExerciseCaloriesDetail(
            @RequestBody ExerciseCalculateRequest request
    ) {
        ExerciseCalculateResponse response = exerciseService.calculateCaloriesDetail(request);
        return ResponseEntity.ok(response);
    }
}