package com.websever.websever.service.map.subway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.response.SubwayPathResponse;
import com.websever.websever.exception.StationNotFoundException;
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


    public Mono<SubwayPathResponse> searchSubwayPath(String region, String departure, String arrival, String time, String dayType) {
        return getStationCoordinate(departure)
                .zipWith(getStationCoordinate(arrival))
                .flatMap(tuple -> {
                    // tuple.getT1() -> 출발역 좌표, tuple.getT2()
                    return callPathApi(
                            tuple.getT1().x(), tuple.getT1().y(),
                            tuple.getT2().x(), tuple.getT2().y()
                    );
                });
    }


    private Mono<Coordinate> getStationCoordinate(String stationName) {

        String cleanedName = stationName.endsWith("역") ? stationName.substring(0, stationName.length() - 1) : stationName;

        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(cleanedName, StandardCharsets.UTF_8);

            String urlString = String.format("%s/searchStation?apiKey=%s&stationName=%s&stationClass=2",
                    BASE_URL, encodedKey, encodedName);
            URI uri = URI.create(urlString);

            return odsayWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("ODsay응답 [" + stationName + "]: " + response))
                    .map(json -> {
                        try {
                            JsonNode root = objectMapper.readTree(json);

                            if (root.has("error")) {
                                throw new RuntimeException("ODsay Error: " + root.get("error").get(0).get("message").asText());
                            }

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
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


    private Mono<SubwayPathResponse> callPathApi(String sx, String sy, String ex, String ey) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);


            String urlString = String.format("%s/searchPubTransPathT?apiKey=%s&SX=%s&SY=%s&EX=%s&EY=%s&SearchPathType=1",
                    BASE_URL, encodedKey, sx, sy, ex, ey);
            URI uri = URI.create(urlString);

            return odsayWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("ODsay 경로 응답 (Raw): " + response)) // 👈 이 줄을 추가합니다.
                    .map(this::parseOdsayResponse);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private SubwayPathResponse parseOdsayResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            if (root.has("error")) {
                throw new RuntimeException("ODsay Error: " + root.get("error").get(0).get("message").asText());
            }
            if (!root.has("result")) {
                throw new RuntimeException("경로를 찾을 수 없습니다.");
            }

            JsonNode path = root.get("result").get("path").get(0);
            JsonNode info = path.get("info");

            int totalTime = info.get("totalTime").asInt();
            int totalFare = info.get("payment").asInt();

            int busTransitCount = info.get("busTransitCount").asInt();
            int subwayTransitCount = info.get("subwayTransitCount").asInt();
            int transferCount = Math.max(0, busTransitCount + subwayTransitCount - 1);

            int stationCount = info.get("totalStationCount").asInt();

            List<SubwayPathResponse.PathSegment> segments = new ArrayList<>();
            JsonNode subPaths = path.get("subPath");

            for (JsonNode subPath : subPaths) {
                if (subPath.get("trafficType").asInt() == 1) { // 1: 지하철
                    String startName = subPath.get("startName").asText();
                    String endName = subPath.get("endName").asText();
                    String lineName = subPath.get("lane").get(0).get("name").asText();
                    int sectionTime = subPath.get("sectionTime").asInt();
                    int count = subPath.get("stationCount").asInt();

                    segments.add(new SubwayPathResponse.PathSegment(startName, endName, lineName, sectionTime, count));
                }
            }

            return new SubwayPathResponse(
                    totalTime,
                    transferCount,
                    stationCount,
                    totalFare,
                    "00:00",
                    "00:00",
                    segments
            );

        } catch (Exception e) {
            throw new RuntimeException("경로 데이터 파싱 오류: " + e.getMessage(), e);
        }
    }

    private record Coordinate(String x, String y) {}
}