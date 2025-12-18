package com.websever.websever.controller.map.bus;

import com.websever.websever.dto.BusRouteDetailDto;
import com.websever.websever.service.map.bus.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transport/bus")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @GetMapping("/search")
    public Mono<ResponseEntity<String>> searchBus(
            @RequestParam String cityCode,
            @RequestParam(required = false) String busNo
    ) {
        return busService.searchBusLane(cityCode, busNo)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/detail")
    public Mono<ResponseEntity<BusRouteDetailDto>> getBusDetail(
            @RequestParam(name = "laneId") String laneId
    ) {
        return busService.getBusLaneDetail(laneId)
                .map(ResponseEntity::ok);
    }
}