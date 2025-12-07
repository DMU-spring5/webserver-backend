package com.websever.websever.service.health;

import com.websever.websever.dto.request.ExerciseCalculateRequest;
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
    public List<ExerciseResponse> getExercises(String name) { // category, tool 제거

        List<ExerciseEntity> exercises;

        // 검색어가 있는 경우: 검색어 우선으로 조회하기 (name만 처리)
        if (name != null && !name.isBlank()) {
            // 이름에 검색어가 포함된 모든 운동 조회
            exercises = exerciseRepository.findByNameContainingIgnoreCase(name);
        }
        // 검색어가 없는 경우: 전체 조회
        else {
            exercises = exerciseRepository.findAllByOrderByNameAsc();
        }
        // 기존의 category, tool 필터링 로직 제거

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

    @Transactional(readOnly = true)
    public ExerciseCalculateResponse calculateCaloriesDetail(ExerciseCalculateRequest request) {
        ExerciseEntity exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("해당 운동을 찾을 수 없습니다. ID: " + request.getExerciseId()));

        // 1. 강도별 계수 설정 (기본값 1.0)
        double intensityMultiplier = 1.0;
        if ("LIGHT".equalsIgnoreCase(request.getIntensity())) {
            intensityMultiplier = 0.8;
        } else if ("HARD".equalsIgnoreCase(request.getIntensity())) {
            intensityMultiplier = 1.2;
        }

        // 2. 체중 반영 비율 (DB의 calories가 70kg 성인 기준 1분 소모량이라고 가정할 때 보정)
        // 공식: (DB칼로리) * (사용자체중 / 70kg) * 강도 * 시간
        double weightRatio = request.getCurrentWeight() / 70.0;

        // 3. 최종 칼로리 계산
        int totalCalories = (int) (exercise.getCalories() * weightRatio * intensityMultiplier * request.getDurationMin());

        return ExerciseCalculateResponse.builder()
                .exerciseName(exercise.getName())
                .durationMin(request.getDurationMin())
                .burnedCalories(totalCalories)
                .build();
    }
}