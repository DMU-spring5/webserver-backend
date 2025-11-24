package com.websever.websever.controller;

import com.websever.websever.dto.SubwayPathResponse;
import com.websever.websever.service.SubwayService;
import lombok.RequiredArgsConstructor;
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

    /*
      지하철 경로 검색 API
      @param region 지역 (수도권, 부산, 대구 등)
      @param departure 출발역
      @param arrival 도착역
      @param time 출발 시간 (HHmm 형식, 예: 0830)
      @param dayType 요일 구분 (WEEKDAY, SATURDAY, HOLIDAY)
      @return 경로 상세 정보
     */
    @GetMapping("/path")
    public Mono<SubwayPathResponse> getSubwayPath(
            @RequestParam String region,
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam String time,
            @RequestParam String dayType) {

        return subwayService.searchSubwayPath(region, departure, arrival, time, dayType);
    }
}