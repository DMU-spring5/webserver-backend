package com.websever.websever.entity.unit;


import com.websever.websever.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Entity
public class UnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "star", length = 5)
    private String star;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "hard", length = 50)
    private String hard;

    @Column(name = "working", length = 50)
    private String working;

    @Column(name="good", length = 50)
    private String good;

    @Column(name="bad", length = 50)
    private String bad;

    @Column(name="hope", length = 50)
    private String hope;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity userId;

}
