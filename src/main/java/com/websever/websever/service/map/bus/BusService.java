package com.websever.websever.service.map.bus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.BusRouteDetailDto;
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
    private final ObjectMapper objectMapper; // JSON 변환기

    // ODsay API 기본 도메인 주소 정의
    private static final String BASE_URL = "https://api.odsay.com/v1/api";

    public BusService(@Qualifier("odsayWebClient") WebClient odsayWebClient,
                      @Value("${api.odsay.key}") String apiKey,
                      ObjectMapper objectMapper) {
        this.odsayWebClient = odsayWebClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    // 1단계: 버스 노선 검색 (기존 유지 - String 반환)
    public Mono<String> searchBusLane(String cityCode, String busNo) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("apiKey=").append(encodedKey);
            queryBuilder.append("&CID=").append(cityCode);

            if (busNo != null && !busNo.isEmpty()) {
                queryBuilder.append("&busNo=").append(URLEncoder.encode(busNo, StandardCharsets.UTF_8));
            }

            URI uri = URI.create(BASE_URL + "/searchBusLane?" + queryBuilder.toString());
            log.info("ODsay 검색 요청 URI: {}", uri);

            return odsayWebClient.get()
                    .uri(uri)
                    .header("Referer", "http://localhost:9191")
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(e -> log.error("ODsay 검색 에러: ", e));

        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * 2단계: 노선 상세 조회 (DTO 반환으로 변경)
     */
    public Mono<BusRouteDetailDto> getBusLaneDetail(String laneId) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            // 파라미터 busID 사용
            String queryString = "apiKey=" + encodedKey + "&busID=" + laneId;

            URI uri = URI.create(BASE_URL + "/busLaneDetail?" + queryString);
            log.info("ODsay 상세 요청 URI: {}", uri);

            return odsayWebClient.get()
                    .uri(uri)
                    .header("Referer", "http://localhost:9191")
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(jsonString -> {
                        try {
                            // JSON 파싱 로직
                            JsonNode root = objectMapper.readTree(jsonString);
                            JsonNode resultNode = root.get("result");

                            if (resultNode == null) {
                                return Mono.error(new RuntimeException("API 응답에 result가 없습니다. 에러: " + root.toString()));
                            }

                            // result 객체를 DTO로 자동 매핑
                            BusRouteDetailDto dto = objectMapper.treeToValue(resultNode, BusRouteDetailDto.class);
                            return Mono.just(dto);

                        } catch (Exception e) {
                            log.error("JSON 변환 중 오류 발생", e);
                            return Mono.error(e);
                        }
                    })
                    .doOnNext(dto -> log.info("상세 조회 성공: {}번 버스", dto.getBusNo()));

        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}