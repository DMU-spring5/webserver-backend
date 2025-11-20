package com.websever.websever.service;

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
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportationService {

    private final RestTemplate restTemplate;
    private final DataCacheRepository dataCacheRepository;

    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.client.secret}")
    private String naverClientSecret;


    private final String geocodingApiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode";

    private final String transitApiUrl = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/transit";



    private HttpHeaders createNaverApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverClientId);
        headers.set("X-NCP-APIGW-API-KEY", naverClientSecret);
        return headers;
    }


    public String searchLocationByQuery(String query) {
        String cacheDataType = "NAVER_GEOCODE_" + query;
        Optional<DataCacheEntity> cachedData = dataCacheRepository
                .findFirstByDataTypeOrderByFetchedAtDesc(cacheDataType);

        if (cachedData.isPresent() && cachedData.get().getFetchedAt().isAfter(OffsetDateTime.now().minusDays(1))) {
            log.info("캐시된 응답 반환: {}", cacheDataType);
            return cachedData.get().getContent();
        }

        log.info("Naver API 호출: {}", cacheDataType);
        HttpHeaders headers = createNaverApiHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(geocodingApiUrl)
                .queryParam("query", query)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, String.class
        );

        String responseBody = response.getBody();
        DataCacheEntity newCache = new DataCacheEntity();
        newCache.setDataType(cacheDataType);
        newCache.setContent(responseBody);
        newCache.setSource("Naver Geocoding");
        dataCacheRepository.save(newCache);

        return responseBody;
    }

    // 지하철 경로 탐색

    public String findSubwayPath(String startStation, String endStation) {


        String cacheDataType = "TRANSIT_PATH_" + startStation + "_" + endStation;


        Optional<DataCacheEntity> cachedData = dataCacheRepository
                .findFirstByDataTypeOrderByFetchedAtDesc(cacheDataType);

        if (cachedData.isPresent() && cachedData.get().getFetchedAt().isAfter(OffsetDateTime.now().minusHours(1))) {
            log.info("캐시된 대중교통 경로 반환: {}", cacheDataType);
            return cachedData.get().getContent();
        }

        // 3. 캐시가 없으면 Naver API 호출
        log.info("Naver 대중교통 API 호출: {}", cacheDataType);
        HttpHeaders headers = createNaverApiHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 4. URL 및 파라미터 빌드 (start, goal 파라미터 사용)
        URI uri = UriComponentsBuilder.fromUriString(transitApiUrl)
                .queryParam("start", startStation)
                .queryParam("goal", endStation)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        // 5. API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, String.class
        );

        String responseBody = response.getBody();

        // 6. 새 데이터를 캐시에 저장
        DataCacheEntity newCache = new DataCacheEntity();
        newCache.setDataType(cacheDataType);
        newCache.setContent(responseBody);
        newCache.setSource("Naver Directions 15");
        dataCacheRepository.save(newCache);

        return responseBody;
    }
}