package com.websever.websever.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record TmapResponse(
        @JsonProperty("type")
        String type,

        @JsonProperty("features")
        List<Map<String, Object>> features
) {
}
