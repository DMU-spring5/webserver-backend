package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubwayPathDto {


    private int globalTravelTime;   // 소요시간 (분)
    private int transferCount;      // 환승 횟수
    private int fare;               // 요금
    private int stationCount;       // 총 정차역 수


    private List<PathSegment> paths;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PathSegment {
        private String laneName;    // 호선 명 (예: 4호선)
        private String startName;   // 승차 역 이름 (예: 서울역)
        private String wayName;
        private int stationCount;
    }
}