package com.websever.websever.entity.news;

import com.websever.websever.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "recommend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Integer recommendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    @CreationTimestamp
    @Column(name = "recommended_at")
    private OffsetDateTime recommendedAt;

    @Column(length = 10)
    @ColumnDefault("'AI'")
    private String type; // 'AI' or 'General'

    @Column(length = 1)
    @ColumnDefault("'N'")
    private String clicked; // 읽음 여부 (Y/N)

    @Column(length = 1)
    @ColumnDefault("'N'")
    private String liked; // 좋아요/스크랩 여부 (Y/N)
}