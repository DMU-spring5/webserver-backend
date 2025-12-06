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

    /**
     * [수정] 칼로리 계산기 로직 (계산 + DB 저장)
     */
    @Transactional
    public CalorieCalculatorDto.Response calculatePlan(String userId, CalorieCalculatorDto.Request request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. BMR, TDEE 계산 (Mifflin-St Jeor 공식)
        double bmr = (10 * request.getCurrentWeight()) + (6.25 * request.getHeight()) - (5 * request.getAge()) + 5;
        double activityMultiplier = getActivityMultiplier(request.getActivityLevel());
        double tdee = bmr * activityMultiplier;

        // 2. 목표 달성 정보 계산
        double dailyDeficit = 500.0;
        double recommendedDailyBurn = tdee + dailyDeficit;

        double weightDiff = request.getCurrentWeight() - request.getTargetWeight();
        int estimatedWeeks = 0;
        if (weightDiff > 0) {
            estimatedWeeks = Math.max(1, (int) ((weightDiff * 7700) / dailyDeficit) / 7);
        } else {
            recommendedDailyBurn = tdee; // 유지 또는 증량 목표 시 기본 유지 칼로리
        }

        HealthGoalEntity goalEntity = healthGoalRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElse(new HealthGoalEntity());

        // 4. 객체에 요청 데이터 및 계산 결과 설정
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

        return CalorieCalculatorDto.Response.builder()
                .bmr(Math.round(bmr * 100.0) / 100.0)
                .tdee(Math.round(tdee * 100.0) / 100.0)
                .recommendedDailyBurn(Math.round(recommendedDailyBurn * 100.0) / 100.0)
                .estimatedWeeks(estimatedWeeks)
                .build();
    }


    /**
     * [신규] 사용자의 가장 최근 목표 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public HealthGoalResponse getLatestGoal(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 가장 최근에 저장된 목표 정보를 가져옴
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

    private double getActivityMultiplier(String level) {
        switch (level) {
            case "VERY_LOW": return 1.2;      // 운동 거의 안함
            case "LOW": return 1.375;         // 주 1-3회
            case "NORMAL": return 1.55;       // 주 3-5회
            case "HIGH": return 1.725;        // 주 6-7회
            case "VERY_HIGH": return 1.9;     // 매일 격렬한 운동/육체노동
            default: return 1.2;
        }
    }
}