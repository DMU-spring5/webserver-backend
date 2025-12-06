package com.websever.websever.dto.health;

import lombok.Builder;
import lombok.Data;

public class CalorieCalculatorDto {

    @Data
    public static class Request {
        // 1단계 정보
        private Integer age;            // 나이
        private Double height;          // 키 (cm)
        private String activityLevel;   // 활동량 (VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH)

        // 2단계 정보
        private Double currentWeight;   // 현재 체중 (kg)
        private Double targetWeight;    // 목표 체중 (kg)

        // 성별 (군인 앱 특성상 남성 기본값이지만, 확장성을 위해 추가 가능)
        private String gender; // "MALE", "FEMALE" (Optional, Default MALE)
    }

    @Data
    @Builder
    public static class Response {
        private Double bmr;                 // 기초대사량 (kcal)
        private Double tdee;                // 활동대사량 (kcal)
        private Double recommendedDailyBurn;// 하루 권장 소모 칼로리 (kcal)
        private Integer estimatedWeeks;     // 예상 실천 기간 (주)
    }
}