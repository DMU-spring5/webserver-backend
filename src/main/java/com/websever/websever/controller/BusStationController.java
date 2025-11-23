package com.websever.websever.controller;

import com.websever.websever.dto.response.BusStationDetailResponse;
import com.websever.websever.dto.response.BusStationSearchResponse;
import com.websever.websever.service.BusStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bus")
@RequiredArgsConstructor
public class BusStationController {

    private final BusStationService busStationService;

    // 요청 예시: http://localhost:9191/api/v1/bus/station?stationID=107475
    @GetMapping("/station")
    public ResponseEntity<BusStationDetailResponse> getStationDetail(@RequestParam("stationID") String stationID) {

        BusStationDetailResponse response = busStationService.getBusStationInfo(stationID);

        // 결과가 없을 경우에 대한 처리는 서비스 로직에 따라 404 등을 리턴할 수도 있음
        if (response == null || response.getResult() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    // 검색 API
    // 요청 예시: http://localhost:9191/api/v1/bus/search?name=서울역
    @GetMapping("/search")
    public ResponseEntity<BusStationSearchResponse> searchStations(@RequestParam("name") String stationName) {

        if (stationName == null || stationName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); // 검색어가 없으면 400 에러
        }

        BusStationSearchResponse response = busStationService.searchStations(stationName);

        // 검색 결과가 없거나 오류인 경우 처리
        if (response == null || response.getResult() == null) {
            // 204 No Content 혹은 빈 객체 반환 등 선택 가능
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

}