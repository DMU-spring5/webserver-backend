package com.websever.websever.dto.response;

import com.websever.websever.entity.news.NewsEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsResponse {
    private Integer newsId;
    private String title;
    private String summary;
    private String imageUrl;
    private String url;
    private String publisher;
    private String publishedAt;

    public static NewsResponse from(NewsEntity entity) {
        return NewsResponse.builder()
                .newsId(entity.getNewsId())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .imageUrl(entity.getImageUrl())
                .url(entity.getUrl())
                .publisher(entity.getPublisher())
                .publishedAt(entity.getPublishedAt() != null ? entity.getPublishedAt().toString() : null)
                .build();
    }
}