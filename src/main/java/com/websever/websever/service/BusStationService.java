package com.websever.websever.service;

import com.websever.websever.dto.BusArrivalResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service

public class BusStationService {

    private final WebClient arrivalWebClient;
    private final String serviceKey;

    public BusStationService(@Qualifier("arrivalWebClient") WebClient arrivalWebClient,
                             @Value("${api.service-key}") String serviceKey) {
        this.arrivalWebClient = arrivalWebClient;
        this.serviceKey = serviceKey;
    }

    /**
     * 정류소 별 버스 도착 예정 정보 목록 조회
     */
    public Mono<List<BusArrivalResponse.Item>> getBusArrivalList(String cityCode, String nodeId) {
        return arrivalWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getSttnAcctoArvlPrearngeInfoList")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("cityCode", cityCode)
                        .queryParam("nodeId", nodeId)
                        .queryParam("numOfRows", "20")
                        .queryParam("pageNo", "1")
                        .queryParam("_type", "json")
                        .build())
                .retrieve()
                .bodyToMono(BusArrivalResponse.class)
                .map(response -> {

                    if (response != null &&
                            response.response() != null &&
                            response.response().body() != null &&
                            response.response().body().items() != null &&
                            response.response().body().items().item() != null) {

                        return response.response().body().items().item();
                    } else {

                        return Collections.<BusArrivalResponse.Item>emptyList();
                    }
                })

                .onErrorResume(e -> {
                    System.out.println("도착 정보 조회 중 오류: " + e.getMessage());

                    return Mono.just(Collections.<BusArrivalResponse.Item>emptyList());
                });
    }
}