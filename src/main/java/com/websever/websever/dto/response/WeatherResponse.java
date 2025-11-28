package com.websever.websever.dto.response;

import com.websever.websever.dto.KmaItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeatherResponse {
    private int nx;
    private int ny;
    private String baseDate;
    private String baseTime;
    private List<KmaItem> items;
}
