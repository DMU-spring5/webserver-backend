package com.websever.websever.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.SubwayPathDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SubwayService {

    private final WebClient odsayWebClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "https://api.odsay.com/v1/api";

    public SubwayService(@Qualifier("odsayWebClient") WebClient odsayWebClient,
                         @Value("${api.odsay.key}") String apiKey,
                         ObjectMapper objectMapper) {
        this.odsayWebClient = odsayWebClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    /**
     * 1. 지하철역 검색 (역 이름 -> ID 반환)
     * 예: "서울역" -> "130"
     */
    public Mono<String> searchStationId(String stationName, String cityCode) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);

            URI uri = URI.create(BASE_URL + "/searchStation?apiKey=" + encodedKey +
                    "&stationName=" + encodedName +
                    "&CID=" + cityCode +
                    "&stationClass=2"); // 2=지하철

            return odsayWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(json -> {
                        try {
                            JsonNode root = objectMapper.readTree(json);
                            // 결과 중 첫 번째 역의 ID만 추출
                            if (root.has("result") && root.get("result").has("station")) {
                                String stationId = root.get("result").get("station").get(0).get("stationID").asText();
                                return Mono.just(stationId);
                            }
                            return Mono.error(new RuntimeException("해당 역을 찾을 수 없습니다: " + stationName));
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    /**
     * 2. 지하철 경로 검색 (출발ID, 도착ID -> 경로 상세 정보)
     */
    public Mono<SubwayPathDto> searchSubwayPath(String startId, String endId, String cityCode) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            // SID: 출발역ID, EID: 도착역ID, Sopt: 1(최단시간)
            URI uri = URI.create(BASE_URL + "/subwayPath?apiKey=" + encodedKey +
                    "&CID=" + cityCode + "&SID=" + startId + "&EID=" + endId + "&Sopt=1");

            log.info("지하철 경로 검색 URI: {}", uri);

            return odsayWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(json -> {
                        try {
                            JsonNode root = objectMapper.readTree(json);


                            if (root.has("error")) {
                                String code = root.get("error").get(0).get("code").asText();
                                String msg = root.get("error").get(0).get("message").asText();
                                return Mono.error(new RuntimeException("ODsay API Error(" + code + "): " + msg));
                            }

                            JsonNode result = root.get("result");
                            if (result == null) {
                                return Mono.error(new RuntimeException("검색된 지하철 경로가 없습니다."));
                            }

                            // DTO 매핑
                            SubwayPathDto dto = new SubwayPathDto();
                            dto.setGlobalTravelTime(result.get("globalTravelTime").asInt());
                            dto.setFare(result.get("fare").asInt());
                            dto.setStationCount(result.get("globalStationCount").asInt());

                            // 상세 경로 (driveInfo) 파싱
                            List<SubwayPathDto.PathSegment> segments = new ArrayList<>();
                            JsonNode driveInfos = result.get("driveInfoSet").get("driveInfo");

                            if (driveInfos.isArray()) {
                                for (JsonNode node : driveInfos) {
                                    SubwayPathDto.PathSegment segment = new SubwayPathDto.PathSegment();
                                    segment.setLaneName(node.get("laneName").asText());
                                    segment.setStartName(node.get("startName").asText());
                                    segment.setWayName(node.get("wayName").asText());
                                    segment.setStationCount(node.get("stationCount").asInt());
                                    segments.add(segment);
                                }
                                dto.setTransferCount(segments.size() - 1); // 환승 횟수 = 경로 개수 - 1
                            }
                            dto.setPaths(segments);

                            return Mono.just(dto);
                        } catch (Exception e) {
                            log.error("지하철 경로 파싱 오류", e);
                            return Mono.error(e);
                        }
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}