package com.websever.websever.dto.response;

import com.websever.websever.entity.health.ExerciseEntity;
import lombok.Data;

@Data
public class ExerciseResponse {
    private Integer exerciseId;
    private String name;
    private String category;
    private String tool;
    private Integer calories;

    public static ExerciseResponse from(ExerciseEntity entity) {
        ExerciseResponse response = new ExerciseResponse();
        response.setExerciseId(entity.getExerciseId());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setTool(entity.getTool());
        response.setCalories(entity.getCalories());
        return response;
    }
}