package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 문서에 없는 필드가 와도 에러 방지
public class SubwayPathDto {
    private Result result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private int globalTravelTime;  // 전체 운행 소요시간(분)
        private int globalDistance;    // 전체 운행거리(km)
        private int globalStationCount;// 전체 정차역 수
        private int fare;              // 카드 요금
        private int cashFare;          // 현금 요금

        private DriveInfoSet driveInfoSet; // 상세 경로 정보 (환승 포함)
        private StationSet stationSet;     // 경유하는 모든 역 정보
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriveInfoSet {
        private List<DriveInfo> driveInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriveInfo {
        private String laneName;   // 승차 노선명 (예: 1호선)
        private String startName;  // 승차역 이름
        private int stationCount;  // 이동 역 수
        private int wayCode;       // 방면 코드 (1:상행, 2:하행)
        private String wayName;    // 방면 이름 (예: 소요산행)
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StationSet {
        private List<Station> stations;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Station {
        private int startID;       // 출발역 ID
        private String startName;  // 출발역 이름
        private int endID;         // 도착역 ID
        private String endName;    // 도착역 이름
        private int travelTime;    // 구간 소요시간
    }
}