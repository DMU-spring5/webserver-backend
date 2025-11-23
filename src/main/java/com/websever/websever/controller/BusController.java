package com.websever.websever.controller;

import com.websever.websever.service.BusService;
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

    /**
     * 버스 노선 검색 API
     * 사용 예: /api/v1/transport/bus/search?cityCode=1000&busNo=150
     */

    @GetMapping("/search")
    public Mono<ResponseEntity<String>> searchBus(
            @RequestParam String cityCode,
            @RequestParam(required = false) String busNo
    ) {
        return busService.searchBusLane(cityCode, busNo)
                .map(ResponseEntity::ok);
    }

    /**
     * 버스 노선 상세 정보 조회 API
     * 사용 예: /api/v1/transport/bus/detail?laneId=12345
     */
    @GetMapping("/detail")
    public Mono<ResponseEntity<String>> getBusDetail(
            @RequestParam String laneId
    ) {
        return busService.getBusLaneDetail(laneId)
                .map(ResponseEntity::ok);
    }
}
