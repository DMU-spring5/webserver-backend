package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// 1단계: 정류장 검색 결과
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusStationDto {
    private String stationName; // 정류장 이름
    private int stationID;      // 정류장 고유 ID (2단계 조회 시 필수)
    private String arsID;       // 정류장 번호 (예: 10-001)

    private String localStationID;
    private double x;           // 경도
    private double y;           // 위도
    private String regionName;  // 지역명 (예: 서울)
    private int cityCode;
}

