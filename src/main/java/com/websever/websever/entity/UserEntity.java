package com.websever.websever.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserEntity {


    @Id //주 키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 사용자 로그인 아이디(NOT NULL UNIQUE)
    @Column(name = "user_id", length = 30, nullable = false, unique = true)
    private String userId;

    // 비밀번호 (NOT NULL)
    @Column(length = 100, nullable = false)
    private String password;

    // 닉네임
    @Column(length = 20)
    private String nickname;

    // 서비스 약관 동의 (DEFAULT 'N')
    @Column(name = "service_agreed", columnDefinition = "CHAR(1)")
    @ColumnDefault("'N'") //Y OR N
    private String serviceAgreed;

    // 위치 정보 약관 동의 (DEFAULT 'N')
    @Column(name = "location_agreed", columnDefinition = "CHAR(1)")
    @ColumnDefault("'N'")
    private String locationAgreed;


    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", columnDefinition = "service_type_enum")
    private ServiceType serviceType;

    // 사단 (NOT NULL)
    @Column(nullable = false)
    private String division;

    // 부대 (NOT NULL)
    @Column(nullable = false)
    private String unit;

    // 입대 날짜 (NOT NULL)
    @Column(name = "enlist_date", nullable = false)
    private LocalDate enlistDate;

    // 프로필 이미지 URL (NOT NULL)
    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    // 생성 시간 (TIMESTAMP DEFAULT NOW())
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    // 수정 시간 (TIMESTAMP DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP) - 트리거 처리
    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;
}
