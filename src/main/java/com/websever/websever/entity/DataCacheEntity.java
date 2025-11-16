package com.websever.websever.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "data_cache")
@Getter
@Setter
public class DataCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dataId;

    @Column(length = 100)
    private String source;

    @Column(name = "data_type", length = 50)
    private String dataType;

    @Column(columnDefinition = "JSON")
    private String content;

    @CreationTimestamp
    @Column(name = "fetched_at", updatable = false)
    private OffsetDateTime fetchedAt;
}