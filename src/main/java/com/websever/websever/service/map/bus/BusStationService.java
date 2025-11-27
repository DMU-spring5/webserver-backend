package com.websever.websever.service.map.bus;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.BusStationDto;
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
public class BusStationService {

    private final WebClient odsayWebClient;

    @Value("${api.odsay.key}")
    private String odsayApiKey;

    private final ObjectMapper objectMapper;
    private static final String ODSAY_BASE_URL = "https://api.odsay.com/v1/api";

    public BusStationService(@Qualifier("odsayWebClient") WebClient odsayWebClient,
                             ObjectMapper objectMapper) {
        this.odsayWebClient = odsayWebClient;

        this.objectMapper = objectMapper;
        // JSON 응답이 단일 객체여도 List로 매핑할 수 있게 설정
        this.objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);




    }

    /**
     * 1단계: 정류장 검색 (ODsay 사용)
     */
    public Mono<List<BusStationDto>> searchStation(String stationName, String x, String y) {
        try {
            String encodedKey = URLEncoder.encode(odsayApiKey, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);

            StringBuilder query = new StringBuilder();
            query.append("apiKey=").append(encodedKey)
                    .append("&stationName=").append(encodedName)
                    .append("&stationClass=1");

            if (x != null && y != null && !x.isEmpty() && !y.isEmpty()) {
                query.append("&x=").append(x).append("&y=").append(y);
            }

            URI uri = URI.create(ODSAY_BASE_URL + "/searchStation?" + query.toString());

            return odsayWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(json -> {
                        try {
                            JsonNode root = objectMapper.readTree(json);
                            if (root.has("error")) {
                                return Mono.error(new RuntimeException("ODsay Error: " + root.get("error").get(0).get("message").asText()));
                            }
                            JsonNode stations = root.path("result").path("station");
                            List<BusStationDto> dtoList = new ArrayList<>();
                            if (stations.isArray()) {
                                for (JsonNode node : stations) {
                                    BusStationDto dto = objectMapper.treeToValue(node, BusStationDto.class);
                                    dtoList.add(dto);
                                }
                            }
                            return Mono.just(dtoList);
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }


}