package com.websever.websever.service.health;

import com.websever.websever.dto.health.CalorieCalculatorDto;
import com.websever.websever.dto.health.HealthGoalResponse;
import com.websever.websever.dto.request.HealthRecordRequest;
import com.websever.websever.dto.response.ExerciseCalculateResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.health.ExerciseEntity;
import com.websever.websever.entity.health.HealthEntity;
import com.websever.websever.entity.health.HealthGoalEntity;
import com.websever.websever.repository.auth.UserRepository;
import com.websever.websever.repository.health.ExerciseRepository;
import com.websever.websever.repository.health.HealthGoalRepository;
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
    private final HealthGoalRepository healthGoalRepository;

    /**
     * 칼로리 계산 및 운동 기록 저장
     */
    @Transactional
    public ExerciseCalculateResponse recordExercise(String userId, HealthRecordRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ExerciseEntity exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("운동 정보를 찾을 수 없습니다."));

        int baseCalories = (exercise.getCalories() != null) ? exercise.getCalories() : 0;
        int totalCalories = baseCalories * request.getDurationMin();

        HealthEntity healthEntity = HealthEntity.builder()
                .user(user)
                .exercise(exercise)
                .durationMin(request.getDurationMin())
                .totalCalories(totalCalories)
                .date(LocalDate.now())
                .build();

        healthRepository.save(healthEntity);

        return ExerciseCalculateResponse.builder()
                .exerciseName(exercise.getName())
                .durationMin(request.getDurationMin())
                .burnedCalories(totalCalories)
                .build();
    }

    @Transactional
    public HealthGoalResponse saveGoal(String userId, CalorieCalculatorDto.Request request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. BMR, TDEE 계산
        double bmr = (10 * request.getCurrentWeight()) + (6.25 * request.getHeight()) - (5 * request.getAge()) + 5;
        double activityMultiplier = getActivityMultiplier(request.getActivityLevel());
        double tdee = bmr * activityMultiplier;

        // 2. 하루 권장 칼로리 계산 (다이어트 목표 시 -500kcal)
        double dailyDeficit = 500.0;
        double recommendedDailyBurn = tdee + dailyDeficit;

        // 3. 목표 Entity 조회 혹은 생성
        HealthGoalEntity goalEntity = healthGoalRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElse(new HealthGoalEntity());

        // 4. 데이터 업데이트 및 저장
        goalEntity.setUser(user);
        goalEntity.setAge(request.getAge());
        goalEntity.setHeight(request.getHeight());
        goalEntity.setActivityLevel(request.getActivityLevel());
        goalEntity.setCurrentWeight(request.getCurrentWeight());
        goalEntity.setTargetWeight(request.getTargetWeight());
        goalEntity.setBmr(bmr);
        goalEntity.setTdee(tdee);
        goalEntity.setTargetCalories(recommendedDailyBurn);

        healthGoalRepository.save(goalEntity);

        // 5. 결과 반환
        return HealthGoalResponse.builder()
                .age(request.getAge())
                .height(request.getHeight())
                .activityLevel(request.getActivityLevel())
                .currentWeight(request.getCurrentWeight())
                .targetWeight(request.getTargetWeight())
                .bmr(Math.round(bmr * 100.0) / 100.0)
                .tdee(Math.round(tdee * 100.0) / 100.0)
                .targetCalories(Math.round(recommendedDailyBurn * 100.0) / 100.0)
                .build();
    }

    @Transactional(readOnly = true)
    public HealthGoalResponse getLatestGoal(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        HealthGoalEntity goal = healthGoalRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new IllegalArgumentException("설정된 목표 정보가 없습니다."));

        return HealthGoalResponse.builder()
                .age(goal.getAge())
                .height(goal.getHeight())
                .activityLevel(goal.getActivityLevel())
                .currentWeight(goal.getCurrentWeight())
                .targetWeight(goal.getTargetWeight())
                .bmr(goal.getBmr())
                .tdee(goal.getTdee())
                .targetCalories(goal.getTargetCalories())
                .build();
    }

    // 활동량에 따른 승수 계산 헬퍼 메서드
    private double getActivityMultiplier(String level) {
        switch (level) {
            case "VERY_LOW": return 1.2;
            case "LOW": return 1.375;
            case "NORMAL": return 1.55;
            case "HIGH": return 1.725;
            case "VERY_HIGH": return 1.9;
            default: return 1.2;
        }
    }
}