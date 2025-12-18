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


    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @RequestParam(required = false) String name
    ) {

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