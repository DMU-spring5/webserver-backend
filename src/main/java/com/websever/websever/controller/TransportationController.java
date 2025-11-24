package com.websever.websever.controller;

import com.websever.websever.service.TransportationService;
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
}