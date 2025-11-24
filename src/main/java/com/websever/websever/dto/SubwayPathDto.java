package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 필요 없는 필드는 무시
public class SubwayPathDto {

    // 1. 요약 정보 (PPT: 소요시간, 환승, 요금)
    private int globalTravelTime;   // 소요시간 (분)
    private int transferCount;      // 환승 횟수 (driveInfo 개수 - 1)
    private int fare;               // 요금
    private int stationCount;       // 총 정차역 수

    // 2. 상세 경로 리스트
    private List<PathSegment> paths;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PathSegment {
        private String laneName;    // 호선 명 (예: 4호선)
        private String startName;   // 승차 역 이름 (예: 서울역)
        private String wayName;     // 방면 (예: 사당 방면)
        private int stationCount;   // 이동 역 수
    }
}