package com.websever.websever.dto.health;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthGoalResponse {
    private Integer age;            // 나이
    private Double height;          // 키
    private String activityLevel;   // 활동량
    private Double currentWeight;   // 현재 체중
    private Double targetWeight;    // 목표 체중
    private Double bmr;             // 기초대사량
    private Double tdee;            // 활동대사량
    private Double targetCalories;  // 목표 칼로리 (하루 권장 소모 칼로리)
}