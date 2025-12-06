package com.websever.websever.dto.request;

import lombok.Data;

@Data
public class ExerciseCalculateRequest {
    private Integer exerciseId; // 운동 ID
    private Integer durationMin; // 운동 시간 (분)
    private String intensity;    // 운동 강도 ("LIGHT", "MODERATE", "HARD")
    private Double currentWeight; // 현재 체중 (kg)
}