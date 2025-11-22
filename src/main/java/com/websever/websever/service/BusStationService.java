package com.websever.websever.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class BusStationService {

    @Value("${odsay.api.key}")
    private String apiKey;

    @Value("${odsay.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public BusStationService() {
        this.restTemplate = new RestTemplate();
    }

    public String getBusStationInfo(String stationID) {
        try {
            // API Key에 특수문자가 포함되어 있으므로 인코딩 처리 (필요 시)
            // ODsay는 보통 Key를 그대로 보내거나, URL 인코딩된 상태로 보내야 합니다.
            // 여기서는 UriComponentsBuilder가 안전하게 처리하도록 합니다.

            // 키가 이미 인코딩된 상태로 올 수도 있고 아닐 수도 있어서,
            // 가장 안전한 방법은 키를 쿼리 파라미터로 직접 문자열에 포함시키는 방법보다
            // UriComponentsBuilder를 사용하는 것입니다.

            // 주의: 이미 발급받은 키에 '%'가 없다면 Raw Key입니다.
            // 하지만 RestTemplate이 '+' 기호를 공백으로 변환할 수 있으므로 인코딩을 명시적으로 합니다.
            String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/busStationInfo")
                    .queryParam("apiKey", apiKey) // RestTemplate이 내부적으로 인코딩을 수행합니다.
                    .queryParam("stationID", stationID)
                    .build()
                    .toUri();

            // 실제 요청 로그 찍어보기 (디버깅용)
            System.out.println("Request URI: " + uri);

            // ODsay API 호출 (결과는 JSON String으로 받음)
            return restTemplate.getForObject(uri, String.class);

        } catch (Exception e) {
            e.printStackTrace();
            return "API 호출 중 오류 발생: " + e.getMessage();
        }
    }
}