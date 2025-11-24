package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
// [중요] 이 어노테이션이 있어야 DTO에 없는 필드가 와도 에러가 안 납니다!
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusRouteDetailDto {

    @JsonProperty("busNo")
    private String busNo;

    @JsonProperty("busID")
    private String busId;

    @JsonProperty("type")
    private String type;

    // [추가된 필드] 에러 원인 해결
    @JsonProperty("busCityName")
    private String busCityName;

    // (혹시 몰라 함께 추가해둠)
    @JsonProperty("busCityCode")
    private int busCityCode;

    @JsonProperty("busCompanyNameKor")
    private String companyName;

    @JsonProperty("busStartPoint")
    private String startPoint;

    @JsonProperty("busEndPoint")
    private String endPoint;

    @JsonProperty("busFirstTime")
    private String firstTime;

    @JsonProperty("busLastTime")
    private String lastTime;

    @JsonProperty("busInterval")
    private String interval;

    @JsonProperty("station")
    private List<StationDto> stations;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true) // 내부 클래스에도 필수
    public static class StationDto {
        @JsonProperty("idx")
        private int sequence;

        @JsonProperty("stationName")
        private String stationName;

        @JsonProperty("stationID")
        private int stationId;

        @JsonProperty("arsID")
        private String arsId;

        @JsonProperty("x")
        private double longitude;

        @JsonProperty("y")
        private double latitude;
    }
}