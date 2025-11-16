package com.websever.websever.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class BusStopResponseDTO {
    private String stopId;       // 정류소 ID
    private String stopName;     // 정류소 이름
    private String direction;    // 진행 방향

}