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

        private int count;
        private List<Station> station;
    }

    @Getter
    @Setter
    @ToString
    public static class Station {
        private String stationName;
        private int stationID;
        private String arsID;
        private double x;
        private double y;
        private String regionName;
        private int stationClass;
    }
}