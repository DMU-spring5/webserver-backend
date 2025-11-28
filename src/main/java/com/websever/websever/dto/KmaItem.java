package com.websever.websever.dto;

import lombok.Data;

@Data
public class KmaItem {
    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private int nx;
    private int ny;
}
