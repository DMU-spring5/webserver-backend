package com.websever.websever.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.request.TmapRequest;
import com.websever.websever.entity.DataCacheEntity;
import com.websever.websever.repository.DataCacheRepository;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class walkingService {

    private final RestTemplate restTemplate;
    private final DataCacheRepository dataCacheRepository;
    private final ObjectMapper objectMapper;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    @Value("${TMAP.APPKEY}")
    private String tmapAppKey;


    private final String geocodingApiUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";

    private final String tmapRouteApiUrl = "https://apis.openapi.sk.com/tmap/routes/pedestrian";

    public record NaverGeocodingCoords(double longitude, double latitude) { }

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

    private NaverGeocodingCoords getCoordsFromQuery(String query) {
        String responseBody = searchLocationByQuery(query);

        try {
            Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);

            @SuppressWarnings("unchecked")
            java.util.List<Map<String, String>> addresses =
                    (java.util.List<Map<String, String>>) jsonMap.get("addresses");

            if (addresses == null || addresses.isEmpty()) {
                throw new IllegalStateException("Naver Geocoding 결과에 좌표가 없습니다: " + query);
            }

            Map<String, String> firstAddress = addresses.get(0);
            double longitude = Double.parseDouble(firstAddress.get("x"));
            double latitude = Double.parseDouble(firstAddress.get("y"));

            return new NaverGeocodingCoords(longitude, latitude);

        } catch (JsonProcessingException | IllegalStateException e) {
            throw new RuntimeException("Naver Geocoding 응답 파싱 실패 또는 좌표 없음", e);
        }
    }

    public String getWalkingRoute(String startAddress, String endAddress) {

        NaverGeocodingCoords startCoords = getCoordsFromQuery(startAddress);
        NaverGeocodingCoords endCoords = getCoordsFromQuery(endAddress);

        TmapRequest tmapRequest = new TmapRequest(
                startCoords.longitude(),
                startCoords.latitude(),
                endCoords.longitude(),
                endCoords.latitude(),
                startAddress,
                endAddress,
                "WGS84GEO",
                "WGS84GEO"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", tmapAppKey);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<TmapRequest> entity = new HttpEntity<>(tmapRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tmapRouteApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String rawJson = response.getBody();

                String prettyJson = objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(
                                objectMapper.readValue(rawJson, Object.class)
                        );

                return prettyJson;
            } else {
                // T Map 응답 오류 처리
                throw new IllegalStateException("T Map API 비정상 응답: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (org.springframework.web.client.RestClientException ex) {
            throw new RuntimeException("T Map API 호출 중 오류 발생: " + ex.getMessage(), ex);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}