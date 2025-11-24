package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusArrivalDto {

    @JsonProperty("result")
    private ArrivalResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArrivalResult {
        @JsonProperty("stationID")
        private int stationId;

        @JsonProperty("stationName")
        private String stationName;


    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArrivalInfo {
        private String busNo;       // 버스 번호
        private String routeId;     // 노선 ID

        private String lowPlate1;   // 저상버스 여부
        private int remainSeatCnt1; // 남은 좌석 수 (빈자리)
    }
}
