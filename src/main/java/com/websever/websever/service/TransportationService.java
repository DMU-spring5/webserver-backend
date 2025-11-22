package com.websever.websever.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.SubwayPathDto;
import com.websever.websever.entity.DataCacheEntity;
import com.websever.websever.repository.DataCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportationService {

    private final RestTemplate restTemplate;
    private final DataCacheRepository dataCacheRepository;
    private final ObjectMapper objectMapper;

    // ▼▼▼ [누락된 부분 추가] ODsay API Key 설정 ▼▼▼
    @Value("${odsay.api.key}")
    private String apiKey;
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    private final String geocodingApiUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";

    // 지하철 경로 검색 URL
    private final String subwayPathUrl = "https://api.odsay.com/v1/api/subwayPath";


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

    // 지하철 경로 조회 메서드
    public SubwayPathDto getSubwayPath(String cid, String sid, String eid) {
        // URI 생성: https://api.odsay.com/v1/api/subwayPath?apiKey={key}&CID={cid}&SID={sid}&EID={eid}
        URI uri = UriComponentsBuilder
                .fromHttpUrl(subwayPathUrl)
                .queryParam("apiKey", apiKey) // 이제 apiKey 변수가 선언되었으므로 에러가 나지 않습니다.
                .queryParam("CID", cid)       // 도시 코드 (수도권: 1000)
                .queryParam("SID", sid)       // 출발역 ID
                .queryParam("EID", eid)       // 도착역 ID
                .build()
                .toUri();

        try {
            // 결과 반환
            return restTemplate.getForObject(uri, SubwayPathDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("지하철 경로 조회 중 오류 발생: " + e.getMessage());
        }
    }
}