package com.websever.websever.service;

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
import java.util.Optional;

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


    public String searchLocationByQuery(String query) {

        String cacheDataType = "NAVER_GEOCODE_" + query;


        Optional<DataCacheEntity> cachedData = dataCacheRepository
                .findFirstByDataTypeOrderByFetchedAtDesc(cacheDataType);

        if (cachedData.isPresent() && cachedData.get().getFetchedAt().isAfter(OffsetDateTime.now().minusDays(1))) {
            return cachedData.get().getContent(); // 캐시된 JSON 반환
        }

        // 3. API 호출을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverClientId);
        headers.set("X-NCP-APIGW-API-KEY", naverClientSecret);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromUriString(geocodingApiUrl)
                .queryParam("query", query)
                .build()
                .encode()
                .toUri();

        // 5. Naver API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody();

        // 6. 새 데이터를 캐시에 저장
        DataCacheEntity newCache = new DataCacheEntity();
        newCache.setDataType(cacheDataType);
        newCache.setContent(responseBody);
        newCache.setSource("Naver Geocoding");
        dataCacheRepository.save(newCache);

        return responseBody;
    }
}