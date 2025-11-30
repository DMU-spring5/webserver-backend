package com.websever.websever.entity.news;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;

    @Column(nullable = false)
    private String title; // 뉴스 제목

    @Column(columnDefinition = "TEXT")
    private String summary; // 뉴스 요약

    @Column(name = "image_url")
    private String imageUrl; // 썸네일 이미지

    @Column(nullable = false)
    private String url; // 기사 원문 링크

    @Column(length = 50)
    private String category; // 카테고리 (사회, 정치 등)

    @Column(length = 100)
    private String publisher; // 언론사

    @Column(name = "published_at")
    private OffsetDateTime publishedAt; // 기사 발행일

    @CreationTimestamp
    @Column(name = "crawled_at", updatable = false)
    private OffsetDateTime crawledAt; // 크롤링된 시간
}