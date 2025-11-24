package com.websever.websever.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.entity.DataCacheEntity;
import com.websever.websever.repository.DataCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportationService {

    private final RestTemplate restTemplate;
    private final DataCacheRepository dataCacheRepository;
    private final ObjectMapper objectMapper;

    @Value("${odsay.api.key}")
    private String odsayApiKey;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    private final String geocodingApiUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";
    private final String odsayBaseUrl = "https://api.odsay.com/v1/api/searchPubTransPath";

    public String searchLocationByQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query는 필수입니다.");
        }

        String normalizedQuery = query.trim().toLowerCase();
        String cacheDataType = "NAVER_GEOCODE_" + normalizedQuery;

        Optional<DataCacheEntity> cachedData = dataCacheRepository
                .findFirstByDataTypeOrderByFetchedAtDesc(cacheDataType);

        if (cachedData.isPresent() &&
                cachedData.get().getFetchedAt().isAfter(OffsetDateTime.now().minusDays(1))) {

            try {
                return objectMapper.writeValueAsString(cachedData.get().getContent());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("캐시 JSON 변환 실패", e);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverClientId);
        headers.set("X-NCP-APIGW-API-KEY", naverClientSecret);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(geocodingApiUrl)
                .queryParam("query", query)
                .build()
                .encode()
                .toUri();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                if (responseBody != null && !responseBody.isBlank()) {

                    Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
                    DataCacheEntity newCache = new DataCacheEntity();
                    newCache.setDataType(cacheDataType);
                    newCache.setContent(jsonMap);
                    newCache.setSource("Naver Geocoding");
                    dataCacheRepository.save(newCache);
                }
                return responseBody;
            } else {

                throw new IllegalStateException("Naver API 비정상 응답: " + response.getStatusCode());
            }
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized ex) {

            throw new RuntimeException("네이버 지도 API 인증 실패 (401). 콘솔에서 Maps API 구독 및 키 확인 필요. " + ex.getMessage(), ex);
        } catch (org.springframework.web.client.HttpClientErrorException ex) {

            throw new RuntimeException("네이버 지도 API 호출 실패 (4xx): " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (org.springframework.web.client.RestClientException ex) {

            throw new RuntimeException("네이버 지도 API 호출 중 오류 발생: " + ex.getMessage(), ex);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


public String searchPubTransPath(double sx, double sy, double ex, double ey) {
    try {
        String encodedKey = URLEncoder.encode(odsayApiKey, StandardCharsets.UTF_8);

        URI uri = UriComponentsBuilder.fromUriString(odsayBaseUrl)
                .queryParam("apiKey", encodedKey)
                .queryParam("SX", sx)
                .queryParam("SY", sy)
                .queryParam("EX", ex)
                .queryParam("EY", ey)
                .queryParam("SearchPathType", 0)
                .build(true)
                .toUri();
        log.info("ODsay Path Request: {}", uri);

        return restTemplate.getForObject(uri, String.class);

    } catch (Exception e) {
        log.error("ODsay 길찾기 호출 중 오류", e);
        throw new RuntimeException("ODsay API 오류", e);
    }
}


    public String getRouteByAddresses(String startAddress, String endAddress) {
        // 1. 출발지 좌표 획득
        Coordinate startCoord = getCoordinateFromAddress(startAddress);
        // 2. 도착지 좌표 획득
        Coordinate endCoord = getCoordinateFromAddress(endAddress);

        // 3. 길찾기 실행
        return searchPubTransPath(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
    }
    private record Coordinate(double x, double y) {}

    // 주소 문자열을 받아 좌표(x, y)를 추출하는 내부 메서드
    private Coordinate getCoordinateFromAddress(String address) {
        String jsonResult = searchLocationByQuery(address);
        try {
            JsonNode root = objectMapper.readTree(jsonResult);
            JsonNode addresses = root.path("addresses");

            if (addresses.isArray() && addresses.size() > 0) {
                JsonNode firstResult = addresses.get(0);
                double x = Double.parseDouble(firstResult.get("x").asText());
                double y = Double.parseDouble(firstResult.get("y").asText());
                return new Coordinate(x, y);
            } else {
                throw new RuntimeException("해당 주소의 좌표를 찾을 수 없습니다: " + address);
            }
        } catch (Exception e) {
            throw new RuntimeException("좌표 변환 중 오류 발생: " + address, e);
        }
    }
}