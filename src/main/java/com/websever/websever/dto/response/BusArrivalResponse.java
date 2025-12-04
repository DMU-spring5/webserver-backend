package com.websever.websever.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BusArrivalResponse(Response response) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(Body body) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(Items items, int totalCount) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(

            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            List<Item> item
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String routeno,             // 버스 번호
            String routetp,             // 버스 유형 (예: 간선, 지선)
            String nodeid,              // 정류소 ID
            String nodenm,              // 정류소명
            Integer arrprevstationcnt,  // 남은 정류장 수
            Integer arrtime,            // 도착 예정 시간
            String nodeord              // 정류소 순번
    ) {}
}