package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusRouteDetailDto {

    @JsonProperty("busNo")
    private String busNo;

    @JsonProperty("busID")
    private String busId;

    @JsonProperty("type")
    private String type;


    @JsonProperty("busCityName")
    private String busCityName;


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
    @JsonIgnoreProperties(ignoreUnknown = true)
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