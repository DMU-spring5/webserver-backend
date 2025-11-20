package com.websever.websever.controller;

import com.websever.websever.dto.BusRouteInfoResponse;
import com.websever.websever.service.BusDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transport")
@RequiredArgsConstructor
public class BusController {

    private final BusDataService busDataService;

    /*
     지역 상세 선택 시 버스 정보 조회 API
     */

    @GetMapping("/bus/route-info")
    public Mono<List<BusRouteInfoResponse.Item>> getBusRouteInfo(
            @RequestParam String cityCode,
            @RequestParam String routeNo) {

        // Service를 호출하여 공공데이터 API로부터 정보를 가져오기
        return busDataService.getBusRouteDetails(cityCode, routeNo);
    }
}
