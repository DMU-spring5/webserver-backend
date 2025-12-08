package com.websever.websever.service.mainpage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${search.client.id}")
    private String clientId;

    @Value("${search.client.secret}")
    private String clientSecret;

    public String searchNews(String query) {
        try {
            String text = URLEncoder.encode(query, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + text;

            HttpURLConnection con = connect(apiURL);
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int responseCode = con.getResponseCode();
            InputStream responseStream = (responseCode == 200)
                    ? con.getInputStream()
                    : con.getErrorStream();

            return readBody(responseStream);  // ★ JSON 문자열 그대로 반환

        } catch (Exception e) {
            throw new RuntimeException("네이버 뉴스 검색 실패", e);
        }
    }

    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            throw new RuntimeException("연결 실패: " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder responseBody = new StringBuilder();
            String line;

            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (Exception e) {
            throw new RuntimeException("API 응답 읽기 실패", e);
        }
    }
}
