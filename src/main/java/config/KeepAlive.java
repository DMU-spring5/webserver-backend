package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class KeepAlive {

    private static final Logger logger = LoggerFactory.getLogger(KeepAlive.class);

    @Value("${service.url:http://localhost:9191}")
    private String serviceUrl;

    @Scheduled(fixedRate = 600000)
    public void keepServiceAlive() {
        if (serviceUrl.contains("localhost")) {
            logger.info("Skip self-ping in local environment.");
            return;
        }

        try {
            URL url = new URL(serviceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                logger.info("ping test " + responseCode);
            } else {
                logger.warn("ping failed" + responseCode);
            }
        } catch (IOException e) {
            logger.error("ping 관련 error" + e.getMessage());
        }
    }
}