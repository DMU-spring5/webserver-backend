package com.websever.websever.service.health;

import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.dto.response.ExerciseResponse;
import com.websever.websever.entity.health.ExerciseEntity;
import com.websever.websever.repository.health.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    /**
     * 운동 카테고리 및 도구별 목록 조회 (PPT 49p ~ 55p)
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercisesByCategoryAndTool(String category, String tool) {

        // 카테고리가 아예 지정되지 않은 경우 (메인 페이지 등)
        if (category == null || category.isBlank()) {
            return exerciseRepository.findAllByOrderByNameAsc().stream()
                    .map(ExerciseResponse::from)
                    .collect(Collectors.toList());
        }

        List<ExerciseEntity> exercises;

        // tool이 null이거나 "전체"인 경우 (1단계 필터링 - 부위 전체 조회)
        if (tool == null || tool.equalsIgnoreCase("전체") || tool.isBlank()) {
            exercises = exerciseRepository.findByCategory(category);
        }
        // category와 tool 모두 지정된 경우 (2단계 필터링 - 부위 + 도구 조회)
        else {
            exercises = exerciseRepository.findByCategoryAndTool(category, tool);
        }

        return exercises.stream()
                .map(ExerciseResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 운동 상세 조회 (PPT 56p)
     */
    @Transactional(readOnly = true)
    public ExerciseResponse getExerciseDetail(Integer exerciseId) {
        ExerciseEntity exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 운동을 찾을 수 없습니다. ID: " + exerciseId));

        return ExerciseResponse.from(exercise);
    }

    /**
     * 운동 예상 소모 칼로리 계산 (PPT 57p)
     * @param exerciseId 운동 ID
     * @param durationMin 운동 시간 (분)
     */
    @Transactional(readOnly = true)
    public ExerciseCalculateResponse calculateCalories(Integer exerciseId, Integer durationMin) {
        ExerciseEntity exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 운동을 찾을 수 없습니다. ID: " + exerciseId));

        // 칼로리 계산 공식: 1분당 칼로리 * 시간(분)
        // (DB의 calories가 1분당 소모 칼로리라고 가정)
        int totalCalories = exercise.getCalories() * durationMin;

        return ExerciseCalculateResponse.builder()
                .exerciseName(exercise.getName())
                .durationMin(durationMin)
                .burnedCalories(totalCalories)
                .build();
    }
}