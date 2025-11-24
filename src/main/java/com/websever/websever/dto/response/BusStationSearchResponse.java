package com.websever.websever.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BusStationSearchResponse {

    private Result result;

    @Getter
    @Setter
    @ToString
    public static class Result {
        // 검색된 정류장 갯수 (선택 사항)
        private int count;
        // 정류장 목록 리스트
        private List<Station> station;
    }

    @Getter
    @Setter
    @ToString
    public static class Station {
        private String stationName;  // 정류장 이름 (예: 서울역)
        private int stationID;       // 정류장 ID (이 값을 상세 조회 API에 사용)
        private String arsID;        // 정류장 고유 번호 (예: 02001)
        private double x;            // 경도
        private double y;            // 위도
        private String regionName;   // 지역명 (예: 서울, 경기)
        private int stationClass;    // 정류장 종류 (1: 버스정류장, 2: 지하철역 등)
    }
}