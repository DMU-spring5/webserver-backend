package com.websever.websever.dto.request;

public record TmapRequest(
        double startX,
        double startY,
        double endX,
        double endY,
        String startName,
        String endName,
        String reqCoordType,
        String resCoordType
) {
}
