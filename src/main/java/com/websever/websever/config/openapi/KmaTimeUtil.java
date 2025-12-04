package com.websever.websever.config.openapi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class KmaTimeUtil {

    private static final int[] BASE_TIMES = {23, 2, 5, 8, 11, 14, 17, 20};

    public static Map<String, String> getBaseDateTime() {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        int hour = now.getHour();

        int baseHour = -1;

        // 현재 시간보다 바로 이전 발표 시간 찾기
        for (int i = BASE_TIMES.length - 1; i >= 0; i--) {
            if (hour >= BASE_TIMES[i]) {
                baseHour = BASE_TIMES[i];
                break;
            }
        }

        // 0~1시: 어제 23시 사용
        if (baseHour == -1) {
            baseHour = 23;
            now = now.minusDays(1);
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = String.format("%02d00", baseHour);

        return Map.of(
                "baseDate", baseDate,
                "baseTime", baseTime
        );
    }
}
