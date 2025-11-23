package com.websever.websever.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.bus.base-url}")
    private String busApiBaseUrl;

    @Bean
    public WebClient busApiWebClient() {
        return WebClient.builder()
                .baseUrl(busApiBaseUrl)
                .defaultHeader("Accept", "application/json") // JSON 응답 요청
                .build();
    }
    @Bean(name = "arrivalWebClient")
    public WebClient arrivalWebClient() {
        return WebClient.builder()
                .baseUrl("http://apis.data.go.kr/1613000/ArvlInfoInqireService")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    @Qualifier("odsayWebClient")
    public WebClient odsayWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.odsay.com/v1/api") // ODsay API 기본 주소
                .defaultHeader(HttpHeaders.REFERER, "localhost:9191")
                .build();
    }
}
