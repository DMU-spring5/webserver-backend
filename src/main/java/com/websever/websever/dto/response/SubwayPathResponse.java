package com.websever.websever.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubwayPathResponse(
        int totalTime,          // 총 소요 시간 (분)
        int transferCount,      // 환승 횟수
        int stationCount,       // 총 정차 역 수
        int totalFare,          // 총 요금 (원)
        String departureTime,
        String arrivalTime,
        List<PathSegment> paths // 상세 경로 (환승 정보 포함)
) {
    public record PathSegment(
            String startName,   // 승차 역 이름
            String endName,     // 하차 역 이름 (또는 환승 역)
            String lineName,    // 호선 정보 (예: 1호선)
            int sectionTime,    // 해당 구간 소요 시간
            int stationCount    // 해당 구간 정차 역 수
    ) {}
}