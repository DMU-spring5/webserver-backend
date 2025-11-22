package com.websever.websever.controller;

import com.websever.websever.service.BusStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bus")
public class BusStationController {

    private final BusStationService busStationService;

    @Autowired
    public BusStationController(BusStationService busStationService) {
        this.busStationService = busStationService;
    }

    // 요청 예시: http://localhost:9191/api/v1/bus/station?stationID=107475
    @GetMapping("/station")
    public String getStationDetail(@RequestParam("stationID") String stationID) {
        return busStationService.getBusStationInfo(stationID);
    }
}