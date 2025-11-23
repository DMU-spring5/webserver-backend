package com.websever.websever.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.dto.response.BusStationDetailResponse;
import com.websever.websever.dto.response.BusStationSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusStationService {

    @Value("${odsay.api.key}")
    private String apiKey;

    @Value("${odsay.base.url}") // application.properties 설정 확인 필요
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 추가됨

    /**
     * ODsay API: 버스정류장 세부 정보 조회
     * @param stationID 정류장 ID (예: 107475)
     * @return BusStationDetailResponse (정류장 정보 및 경유 노선 목록)
     */
    public BusStationDetailResponse getBusStationInfo(String stationID) {
        try {
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/busStationInfo")
                    .queryParam("apiKey", apiKey)
                    .queryParam("stationID", stationID)
                    .build()
                    .toUri();

            log.info("ODsay API 호출 URL: {}", uri);

            return restTemplate.getForObject(uri, BusStationDetailResponse.class);

        } catch (Exception e) {
            log.error("ODsay API 호출 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("버스 정류장 정보를 가져오는데 실패했습니다.");
        }
    }

    /**
     * ODsay API: 버스 정류장 이름으로 검색 (searchStation)
     * @param stationName 검색할 정류장 이름 (예: 서울역)
     * @return BusStationSearchResponse (검색된 정류장 목록)
     */
    public BusStationSearchResponse searchStations(String stationName) {
        try {
            // 1. API Key 인코딩
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            // 2. 정류장 이름 인코딩
            String encodedName = URLEncoder.encode(stationName, StandardCharsets.UTF_8);

            // 3. ODsay searchStation 호출 URI 생성
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/searchStation")
                    .queryParam("apiKey", apiKey)
                    .queryParam("stationName", stationName)
                    .queryParam("stationClass", "1") // 1: 버스정류장만 필터링
                    .build()
                    .toUri();

            log.info("ODsay 정류장 검색 API 호출: {}", uri);

            // ---------------------------------------------------------
            // [수정된 부분] 원본 JSON을 확인하기 위해 String으로 먼저 받고 로그 출력 후 변환
            // ---------------------------------------------------------

            // 1. 응답을 String(문자열)으로 받습니다.
            String rawResponse = restTemplate.getForObject(uri, String.class);

            // 2. 로그로 원본 JSON을 출력합니다. (이 부분이 핵심입니다!)
            log.info("ODsay Raw Response for {}: {}", stationName, rawResponse);

            // 2. API 키 에러인지 확인 (임시 방편)
            if (rawResponse != null && rawResponse.contains("\"error\":")) {
                log.error("ODsay API 인증 실패 또는 에러 발생: {}", rawResponse);
                return null; // 또는 예외를 던져서 프론트에 알림
            }
            // 3. String을 DTO 객체로 수동 변환하여 리턴합니다.
            if (rawResponse == null || rawResponse.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(rawResponse, BusStationSearchResponse.class);

        } catch (Exception e) {
            log.error("정류장 검색 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("정류장 검색에 실패했습니다.");
        }
    }
}