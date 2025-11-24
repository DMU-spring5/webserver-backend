package com.websever.websever.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    @Value("${api.odsay.key}") // application.properties 키 이름 확인 필요
    private String odsayApiKey;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    private final String geocodingApiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";
    private final String odsayBaseUrl = "https://api.odsay.com/v1/api/searchPubTransPath";

    /**
     * 1. 네이버 지오코딩 (장소 검색 -> 좌표 변환)
     */
    public String searchLocationByQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query는 필수입니다.");
        }

    public String searchLocationByQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query는 필수입니다.");
        }

        // 캐시 확인
        Optional<DataCacheEntity> cachedData = dataCacheRepository
                .findFirstByDataTypeOrderByFetchedAtDesc(cacheDataType);

        if (cachedData.isPresent() && cachedData.get().getFetchedAt().isAfter(OffsetDateTime.now().minusDays(1))) {
            try {
                return objectMapper.writeValueAsString(cachedData.get().getContent());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("캐시 데이터 변환 중 오류 발생", e);
            }
        }

        // API 호출 헤더 설정
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
                // 정상 응답 시 DB에 캐시 저장
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
        } catch (Exception ex) {
            throw new RuntimeException("네이버 지도 API 호출 실패: " + ex.getMessage(), ex);
        }
    }

    /**
     * 2. ODsay 대중교통 길찾기 (좌표 기반)
     * (아까 코드에서 이 메서드의 앞부분이 날아가고 뒷부분만 남아있어서 에러가 났던 것입니다.)
     */
    public String searchPubTransPath(double sx, double sy, double ex, double ey) {
        try {
            String encodedKey = URLEncoder.encode(odsayApiKey, StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder.fromUriString(odsayBaseUrl)
                    .queryParam("apiKey", encodedKey)
                    .queryParam("SX", sx)
                    .queryParam("SY", sy)
                    .queryParam("EX", ex)
                    .queryParam("EY", ey)
                    .queryParam("SearchPathType", 0) // 0: 모두
                    .build(true)
                    .toUri();

            log.info("ODsay Path Request: {}", uri);

            return restTemplate.getForObject(uri, String.class);

        } catch (Exception e) {
            log.error("ODsay 길찾기 호출 중 오류", e);
            throw new RuntimeException("ODsay API 오류", e);
        }
    }

    /**
     * 3. 통합 길찾기 (주소 텍스트 -> 좌표 변환 -> 길찾기)
     */
    public String getRouteByAddresses(String startAddress, String endAddress) {
        // 1. 출발지 좌표 획득
        Coordinate startCoord = getCoordinateFromAddress(startAddress);
        // 2. 도착지 좌표 획득
        Coordinate endCoord = getCoordinateFromAddress(endAddress);

        // 3. 길찾기 실행
        return searchPubTransPath(startCoord.x, startCoord.y, endCoord.x, endCoord.y);
    }

    // 내부 헬퍼 클래스 (Java 16+ Record)
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

    /**
     * (참고용) 임시 메서드 - 필요 없으면 삭제 가능
     */
    public String findSubwayPath(String start, String end) {
        return "{\"result\": \"success\", \"message\": \"Path finding logic is not implemented yet.\"}";
    }
}