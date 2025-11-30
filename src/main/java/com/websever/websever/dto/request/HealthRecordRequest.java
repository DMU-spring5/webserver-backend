package com.websever.websever.dto.request;

import lombok.Data;

@Data
public class HealthRecordRequest {
    private Integer exerciseId; // 어떤 운동을 했는지
    private Integer durationMin; // 몇 분 동안 했는지
}
