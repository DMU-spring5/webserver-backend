package com.websever.websever.config.openapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {



    @Bean
    @Qualifier("odsayWebClient")
    public WebClient odsayWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.odsay.com/v1/api") //
                .defaultHeader(HttpHeaders.REFERER, "http://localhost:9191")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}