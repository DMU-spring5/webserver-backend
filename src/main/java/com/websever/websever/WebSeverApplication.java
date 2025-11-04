package com.websever.websever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSeverApplication.class, args);
    }

}
