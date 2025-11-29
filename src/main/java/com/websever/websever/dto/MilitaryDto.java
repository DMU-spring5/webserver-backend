package com.websever.websever.dto;

import lombok.Data;

@Data
public class MilitaryDto {
    private String nowRank;             // 현재 계급
    private String nextRank;            // 다음 계급 (또는 '전역')
    private double dischargeProgress;       // 전역까지의 진행률 (%)
    private long daysToDischarge;  // 전역까지 남은 일수 (일)
    private double nextRankProgress;        // 다음 계급까지의 진행률 (%)
    private long daysToNextRank;   // 다음 진급까지 남은 일수 (일)
}
