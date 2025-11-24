package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BusRouteDetailDto {

    @JsonProperty("busNo")
    private String busNo;           // 버스 번호 (예: 150)

    @JsonProperty("busID")
    private String busId;           // 노선 ID (시스템 내부용)

    @JsonProperty("busCompanyNameKor")
    private String companyName;     // 운수회사 (예: 서울교통네트웍)

    @JsonProperty("busStartPoint")
    private String startPoint;      // 기점 (예: 도봉산역)

    @JsonProperty("busEndPoint")
    private String endPoint;        // 종점 (예: 시흥대교)

    @JsonProperty("busFirstTime")
    private String firstTime;       // 첫차 시간 (예: 03:57)

    @JsonProperty("busLastTime")
    private String lastTime;        // 막차 시간 (예: 22:00)

    @JsonProperty("busInterval")
    private String interval;        // 배차 간격 (분)

    // 2. 노선도 (정류장 목록)
    @JsonProperty("station")
    private List<StationDto> stations;

    @Data
    public static class StationDto {
        @JsonProperty("idx")
        private int sequence;       // 순번

        @JsonProperty("stationName")
        private String stationName; // 정류장 이름

        @JsonProperty("stationID")
        private int stationId;      // 정류장 고유 ID

        @JsonProperty("arsID")
        private String arsId;       // 정류장 번호 (예: 10-001)

        @JsonProperty("x")
        private double longitude;   // 경도 (지도 표시용)

        @JsonProperty("y")
        private double latitude;    // 위도 (지도 표시용)
    }
}
