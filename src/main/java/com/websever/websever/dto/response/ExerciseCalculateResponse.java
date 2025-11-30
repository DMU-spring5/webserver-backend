package com.websever.websever.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseCalculateResponse {
    private String exerciseName;    // 운동 이름
    private Integer durationMin;    // 운동 시간(분)
    private Integer burnedCalories; // 예상 소모 칼로리 (kcal)
}