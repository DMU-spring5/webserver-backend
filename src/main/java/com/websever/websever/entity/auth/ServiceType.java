package com.websever.websever.entity.auth;

import java.util.*;
import java.util.stream.Collectors;

public enum ServiceType {
    army("육군", 18, Map.of(
            "이병", 0,
            "일병", 3,
            "상병", 8,
            "병장", 15
    )),
    // 해군: 20개월
    navy("해군", 20, Map.of(
            "이병", 0,
            "일병", 3,
            "상병", 9,
            "병장", 17
    )),
    // 공군: 21개월
    air_force("공군", 21, Map.of(
            "이병", 0,
            "일병", 7,
            "상병", 13,
            "병장", 19
    ));

    private final String koreanName;
    private final int totalMonths;
    private final Map<String, Integer> promotionMonths; //계급명, 복무 개월

    ServiceType(String koreanName, int totalMonths, Map<String, Integer> promotionMonths) {
        this.koreanName = koreanName;
        this.totalMonths = totalMonths;
        this.promotionMonths = promotionMonths.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public String getKoreanName() { return koreanName; }
    public int getTotalMonths() { return totalMonths; }
    public Map<String, Integer> getPromotionMonths() { return promotionMonths; }
}
