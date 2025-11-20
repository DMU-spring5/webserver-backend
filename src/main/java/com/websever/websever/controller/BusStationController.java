package com.websever.websever.controller;

import com.websever.websever.dto.BusArrivalResponse;
import com.websever.websever.service.BusStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transport/station")
@RequiredArgsConstructor
public class BusStationController {

    private final BusStationService busStationService;


    @GetMapping("/arrival")
    public Mono<List<BusArrivalResponse.Item>> getBusArrivalInfo(
            @RequestParam String cityCode,
            @RequestParam String nodeId) {

        return busStationService.getBusArrivalList(cityCode, nodeId);
    }
}