package com.websever.websever.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.SubwayPathResponse;
import com.websever.websever.exception.StationNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubwayService {

    private final WebClient odsayWebClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public SubwayService(@Qualifier("odsayWebClient") WebClient odsayWebClient,
                         @Value("${api.odsay.key}") String apiKey,
                         ObjectMapper objectMapper) {
        this.odsayWebClient = odsayWebClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    /**
     * 실제 지하철 경로 검색 (ODsay API 연동)
     */
    public Mono<SubwayPathResponse> searchSubwayPath(String region, String departure, String arrival, String time, String dayType) {
        // 1. 출발역 좌표 구하기 -> 2. 도착역 좌표 구하기 -> 3. 경로 검색 (체이닝)
        return getStationCoordinate(departure)
                .zipWith(getStationCoordinate(arrival))
                .flatMap(tuple -> {
                    String startX = tuple.getT1().x(); // 필드명 그대로 메서드 호출

                    String startY = tuple.getT1().y();
                    String endX = tuple.getT2().x();
                    String endY = tuple.getT2().y();

                    return callPathApi(startX, startY, endX, endY);
                });
    }

    /**
     * 역 이름으로 좌표(X, Y) 조회
     * API: searchStation
     */
    private Mono<Coordinate> getStationCoordinate(String stationName) {

        String cleanedName= stationName.endsWith("역")? stationName.substring(0, stationName.length()-1) : stationName;


        return odsayWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchStation")
                        .queryParam("apiKey", apiKey)
                        .queryParam("stationName", cleanedName)
                        .queryParam("stationClass", "2") // 2: 지하철
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response-> System.out.println("ODsay응답 ["+ stationName + "]: " + response))

                .map(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);

                        boolean isStationFound = root.has("result") &&
                                root.get("result").has("station") &&
                                root.get("result").get("station").isArray() &&
                                root.get("result").get("station").size() > 0;


                        if (isStationFound) {

                            JsonNode station = root.get("result").get("station").get(0);
                            return new Coordinate(station.get("x").asText(), station.get("y").asText());
                        }

                        throw new StationNotFoundException("지하철 역 정보를 찾을 수 없습니다: " + stationName);

                    } catch (StationNotFoundException e) {

                        throw e;

                    } catch (Exception e) {
                        throw new RuntimeException("좌표 변환 중 기술적 오류 발생: " + stationName, e);
                    }
                });
    }

    /**
     * 좌표를 이용한 대중교통 길찾기
     * API: searchPubTransPathT
     */
    private Mono<SubwayPathResponse> callPathApi(String sx, String sy, String ex, String ey) {
        return odsayWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/searchPubTransPathT")
                        .queryParam("apiKey", apiKey)
                        .queryParam("SX", sx).queryParam("SY", sy)
                        .queryParam("EX", ex).queryParam("EY", ey)
                        .queryParam("SearchPathType", "1") // 1: 지하철만 이용
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseOdsayResponse);
    }


    private SubwayPathResponse parseOdsayResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            if (!root.has("result")) {
                throw new RuntimeException("경로를 찾을 수 없습니다.");
            }

            // 첫 번째 추천 경로(최적 경로) 가져오기
            JsonNode path = root.get("result").get("path").get(0);
            JsonNode info = path.get("info");

            int totalTime = info.get("totalTime").asInt();
            int totalFare = info.get("payment").asInt();
            int transferCount = info.get("busTransitCount").asInt() + info.get("subwayTransitCount").asInt() - 1;
            if (transferCount < 0) transferCount = 0;
            int stationCount = info.get("totalStationCount").asInt();

            List<SubwayPathResponse.PathSegment> segments = new ArrayList<>();
            JsonNode subPaths = path.get("subPath");

            for (JsonNode subPath : subPaths) {
                if (subPath.get("trafficType").asInt() == 1) { // 1: 지하철
                    String startName = subPath.get("startName").asText();
                    String endName = subPath.get("endName").asText();
                    String lineName = subPath.get("lane").get(0).get("name").asText(); // 호선 정보
                    int sectionTime = subPath.get("sectionTime").asInt();
                    int count = subPath.get("stationCount").asInt();

                    segments.add(new SubwayPathResponse.PathSegment(startName, endName, lineName, sectionTime, count));
                }
            }

            // 여기서는 빈 문자열로 둡니다 (프론트에서 계산 가능)
            return new SubwayPathResponse(
                    totalTime,
                    transferCount,
                    stationCount,
                    totalFare,
                    "00:00", // 출발 시간
                    "00:00", // 도착 시간
                    segments
            );

        } catch (Exception e) {
            throw new RuntimeException("경로 데이터 파싱 오류", e);
        }
    }

    private record Coordinate(String x, String y) {}
}