package com.websever.websever.service.map.bus;

import com.websever.websever.dto.response.BusRouteInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusDataService {

    private final WebClient busApiWebClient;

    @Value("${api.service-key}")
    private String serviceKey;


    public Mono<List<BusRouteInfoResponse.Item>> getBusRouteDetails(String cityCode, String routeNo) {
        // 1. 노선 ID 조회 -> 2. 상세 정보 조회
        return getRouteId(cityCode, routeNo)
                .flatMap(routeId -> getRouteInfo(cityCode, routeId));
    }

    private Mono<String> getRouteId(String cityCode, String routeNo) {
        return busApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getRouteNoList")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("cityCode", cityCode)
                        .queryParam("routeNo", routeNo)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(BusRouteInfoResponse.class)
                .map(response -> {

                    if (response != null &&
                            response.response() != null &&
                            response.response().body() != null &&
                            response.response().body().totalCount() > 0 &&
                            response.response().body().items() != null &&
                            response.response().body().items().item() != null &&
                            !response.response().body().items().item().isEmpty()) {

                        // 검색 결과 중 첫 번째 항목의 routeId 반환
                        return response.response().body().items().item().get(0).routeid();
                    } else {

                        throw new RuntimeException("해당 버스 노선을 찾을 수 없습니다. (지역코드: " + cityCode + ", 버스번호: " + routeNo + ")");
                    }
                });
    }


    private Mono<List<BusRouteInfoResponse.Item>> getRouteInfo(String cityCode, String routeId) {
        return busApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getRouteInfoIem")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("cityCode", cityCode)
                        .queryParam("routeId", routeId)
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(BusRouteInfoResponse.class)
                .map(response -> {
                    if (response != null &&
                            response.response() != null &&
                            response.response().body() != null &&
                            response.response().body().items() != null &&
                            response.response().body().items().item() != null) {

                        return response.response().body().items().item();
                    } else {
                        throw new RuntimeException("노선 상세 정보를 불러올 수 없습니다.");
                    }
                });
    }
}