package com.websever.websever.dto.response;

import com.websever.websever.entity.health.ExerciseEntity;
import lombok.Data;

@Data
public class ExerciseResponse {
    private Integer exerciseId;
    private String name;
    private String category;
    private String tool; // 새로 추가
    private Integer calories;

    public static ExerciseResponse from(ExerciseEntity entity) {
        ExerciseResponse response = new ExerciseResponse();
        response.setExerciseId(entity.getExerciseId());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setTool(entity.getTool()); // 새로 추가
        response.setCalories(entity.getCalories());
        return response;
    }
}