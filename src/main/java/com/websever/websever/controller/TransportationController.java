package com.websever.websever.controller;

import com.websever.websever.service.TransportationService;
import com.websever.websever.service.walkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transport")
@RequiredArgsConstructor
public class TransportationController {

    private final TransportationService transportationService;
    private final walkingService walkingService;


    @GetMapping("/search")
    public ResponseEntity<String> searchLocation(@RequestParam String query) {

        String resultJson = transportationService.searchLocationByQuery(query);
        return ResponseEntity.ok(resultJson);
    }
    //지하철 경로 상세 조회
    @GetMapping("/subway-path")
    public ResponseEntity<String> searchSubwayPath(
            @RequestParam String start,
            @RequestParam String end
    ) {
        String resultJson = transportationService.findSubwayPath(start, end);
        return ResponseEntity.ok(resultJson);
    }

    @GetMapping("/route/walking")
    public ResponseEntity<String> getWalkingRoute(
            @RequestParam String startAddress,
            @RequestParam String endAddress) {

        // TransportationService의 getWalkingRoute 메서드를 호출합니다.
        // 이 메서드 내부에서 Naver Geocoding 2회 호출 및 T Map API 호출이 순차적으로 처리됩니다.
        String routeJson = walkingService.getWalkingRoute(startAddress, endAddress);

        // T Map 경로 결과 JSON을 그대로 반환합니다.
        return ResponseEntity.ok(routeJson);
    }
}