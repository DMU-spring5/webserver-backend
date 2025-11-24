package com.websever.websever.controller;

import com.websever.websever.dto.SubwayPathDto;
import com.websever.websever.service.SubwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transport/subway")
@RequiredArgsConstructor
public class SubwayController {

    private final SubwayService subwayService;

    /**
     * 지하철 경로 검색 API (로그인 필수)
     * 1. 출발역, 도착역 이름을 받아서 ID로 변환
     * 2. 변환된 ID로 경로 검색 수행
     * 사용 예: GET /api/v1/transport/subway/path?start=서울역&end=강남&cityCode=1000
     */
    @GetMapping("/path")
    public Mono<ResponseEntity<SubwayPathDto>> searchPath(
            @RequestParam String start,    // 출발역 이름 (예: 서울역)
            @RequestParam String end,      // 도착역 이름 (예: 강남)
            @RequestParam String cityCode  // 도시 코드 (수도권: 1000)
    ) {
        return subwayService.searchStationId(start, cityCode)
                .flatMap(startId ->
                        subwayService.searchStationId(end, cityCode)
                                .flatMap(endId -> subwayService.searchSubwayPath(startId, endId, cityCode))
                )
                .map(ResponseEntity::ok)
                .onErrorResume(e->{
                    return Mono.just(ResponseEntity.badRequest().build());

                });

    }
}