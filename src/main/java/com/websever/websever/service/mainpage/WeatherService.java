package com.websever.websever.service.mainpage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websever.websever.config.openapi.KmaTimeUtil;
import com.websever.websever.dto.response.KmaResponse;
import com.websever.websever.dto.response.WeatherResponse;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${kma.service.key}")
    private String serviceKey;

    private static final int nx = 58;
    private static final int ny = 125;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final List<String> REQUIRED = List.of("TMP", "WSD", "SKY", "POP", "REH");

    public WeatherResponse getWeatherFixedLocation() {
        return getWeather(nx, ny);
    }

    public WeatherResponse getWeather(int nx, int ny) {
        String baseDate = "20251210";
        String baseTime = "0500";
        final String BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

        log.debug("Using fixed baseDate: {} and baseTime: {}", baseDate, baseTime);

        String apiUrl = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 15)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUriString();

        log.debug("KMA API URL: {}", apiUrl);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("기상청 API 호출 실패. Status: {}", response.getStatusCode());
            throw new RuntimeException("기상청 API 호출 실패: " + response.getStatusCode());
        }

        // JSON 파싱
        KmaResponse kma;
        try {
            kma = objectMapper.readValue(response.getBody(), KmaResponse.class);
        } catch (Exception e) {
            log.error("기상청 응답 JSON 파싱 실패", e);
            throw new RuntimeException("기상청 응답 파싱 실패", e);
        }

        // 기상청 내부 resultCode 체크
        var header = kma.getResponse().getHeader();
        String resultCode = header.getResultCode();
        String resultMsg = header.getResultMsg();

        if (!"00".equals(resultCode)) {
            log.error("기상청 내부 오류 발생. Code: {}, Msg: {}", resultCode, resultMsg);
            throw new RuntimeException("기상청 오류: " + resultMsg);
        }

        var items = kma.getResponse().getBody().getItems().getItem();

        if (items == null || items.isEmpty()) {
            log.warn("날씨 데이터가 존재하지 않습니다. KMA API returned no items.");
            throw new RuntimeException("날씨 데이터가 존재하지 않습니다.");
        }

        var filtered = items.stream()
                .filter(i -> REQUIRED.contains(i.getCategory()))
                .toList();

        log.info("items: {}, categories: {}, items: {}",
                items.size(), REQUIRED, filtered.size());

        return new WeatherResponse(nx, ny, baseDate, baseTime, filtered);
    }
}