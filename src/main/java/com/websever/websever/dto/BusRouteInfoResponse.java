package com.websever.websever.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusRouteInfoResponse(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Body body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(Items items, int totalCount) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            @JsonFormat(with= JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

            List<Item> item
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String routeid,     // 노선 ID
            String routeno,     // 노선 번호
            String routetp,     // 노선 유형 (예: "간선버스")
            String startnodenm, // (출발)
            String endnodenm,   // 종점 (도착)
            String startvehicletime, // 첫차 시간
            String endvehicletime,   // 막차 시간
            String intervaltime,     // 배차 간격 (평일)
            String intervalwday,     // 배차 간격 (주말)
            String intervalhd,       // 배차 간격 (공휴일)
            String citycode      // 도시 코드
    ) {}
}
