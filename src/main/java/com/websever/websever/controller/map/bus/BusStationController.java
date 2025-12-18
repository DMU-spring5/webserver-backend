package com.websever.websever.controller.map.bus;

import com.websever.websever.dto.BusStationDto;
import com.websever.websever.service.map.bus.BusStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transport/bus/station")
@RequiredArgsConstructor
public class BusStationController {

    private final BusStationService busStationService;


    @GetMapping("/search")
    public Mono<ResponseEntity<List<BusStationDto>>> searchStation(
            @RequestParam String name,
            @RequestParam(required = false) String x,
            @RequestParam(required = false) String y
    ) {
        return busStationService.searchStation(name, x, y)
                .map(ResponseEntity::ok);
    }


}