package com.websever.websever.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class BusService {

    private final WebClient odsayWebClient;
    private final String apiKey;

    // ODsay API 기본 도메인 주소 정의
    private static final String BASE_URL = "https://api.odsay.com/v1/api";

    public BusService(@Qualifier("odsayWebClient") WebClient odsayWebClient,
                      @Value("${api.odsay.key}") String apiKey) {
        this.odsayWebClient = odsayWebClient;
        this.apiKey = apiKey;
    }

    /**
     * 1단계: 버스 노선 검색
     */
    public Mono<String> searchBusLane(String cityCode, String busNo) {
        try {
            // 1. API Key 수동 인코딩
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            // 2. 쿼리 스트링 조립
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("apiKey=").append(encodedKey);
            queryBuilder.append("&CID=").append(cityCode);

            if (busNo != null && !busNo.isEmpty()) {
                queryBuilder.append("&busNo=").append(URLEncoder.encode(busNo, StandardCharsets.UTF_8));
            }

            // 3. 최종 URI 생성 (반드시 도메인 포함!)
            // 변경 전: "/searchBusLane?..." -> 로컬호스트로 접속 시도 (오류 원인)
            // 변경 후: BASE_URL + "/searchBusLane?..." -> 외부 서버로 정상 접속
            URI uri = URI.create(BASE_URL + "/searchBusLane?" + queryBuilder.toString());

            log.info("ODsay 요청 URI: {}", uri);

            return odsayWebClient.get()
                    .uri(uri)
                    .header("Referer", "http://localhost:9191")
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> log.info("ODsay 응답 성공: {}", response))
                    .doOnError(e -> log.error("ODsay 통신 에러: ", e));

        } catch (Exception e) {
            log.error("URI 생성 중 오류 발생", e);
            return Mono.error(e);
        }
    }

    /**
     * 2단계: 노선 상세 조회
     */
    public Mono<String> getBusLaneDetail(String laneId) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            String queryString = "apiKey=" + encodedKey + "&laneID=" + laneId;

            // 여기도 도메인 추가
            URI uri = URI.create(BASE_URL + "/busLaneDetail?" + queryString);

            return odsayWebClient.get()
                    .uri(uri)
                    .header("Referer", "http://localhost:9191")
                    .retrieve()
                    .bodyToMono(String.class);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}