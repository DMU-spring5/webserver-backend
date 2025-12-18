package com.websever.websever.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BusStationDetailResponse {

    private Result result;

    @Getter
    @Setter
    @ToString
    public static class Result {
        private String stationName; // 정류장 이름
        private int stationID;      // 정류장 ID
        private double x;           // 경도 (X좌표)
        private double y;           // 위도 (Y좌표)
        private String arsID;       // 정류장 고유 번호 (5자리)
        private List<Lane> lane;    // 경유 버스 노선 리스트
    }

    @Getter
    @Setter
    @ToString
    public static class Lane {
        private String busNo;
        private int type;
        private int busID;
        private String busCityName;
    }
}