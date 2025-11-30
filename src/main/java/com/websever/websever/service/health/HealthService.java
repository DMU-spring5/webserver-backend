package com.websever.websever.service.health;

import com.websever.websever.dto.request.HealthRecordRequest;
import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.health.ExerciseEntity;
import com.websever.websever.entity.health.HealthEntity;
import com.websever.websever.repository.auth.UserRepository;
import com.websever.websever.repository.health.ExerciseRepository;
import com.websever.websever.repository.health.HealthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthRepository healthRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    /**
     * 칼로리 계산 및 운동 기록 저장 (차별화된 기능)
     * POST 요청 시 계산 결과를 DB에 영구 저장합니다.
     */
    @Transactional
    public ExerciseCalculateResponse recordExercise(String userId, HealthRecordRequest request) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 운동 정보 조회
        ExerciseEntity exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("운동 정보를 찾을 수 없습니다."));

        // 3. 칼로리 계산
        int baseCalories = (exercise.getCalories() != null) ? exercise.getCalories() : 0;
        int totalCalories = baseCalories * request.getDurationMin();

        // 4. DB에 저장 (이 부분이 차별점!)
        HealthEntity healthEntity = HealthEntity.builder()
                .user(user)
                .exercise(exercise)
                .durationMin(request.getDurationMin())
                .totalCalories(totalCalories)
                .date(LocalDate.now()) // 오늘 날짜로 기록
                .build();

        healthRepository.save(healthEntity);

        // 5. 결과 반환 (화면에 보여줄 데이터)
        return ExerciseCalculateResponse.builder()
                .exerciseName(exercise.getName())
                .durationMin(request.getDurationMin())
                .burnedCalories(totalCalories)
                .build();
    }
}