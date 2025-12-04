package com.websever.websever.entity.health;

import com.websever.websever.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Integer exerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private UserEntity user;

    @Column(length = 100, nullable = false)
    private String name; // 운동 이름 (예: 덤벨 컬, 벤치 프레스)

    @Column(length = 50)
    private String category; // 운동 부위 (예: 어깨&팔, 가슴, 등, 하체, 유산소, 스포츠)

    @Column(length = 50)
    private String tool; // 운동 도구 (예: 덤벨, 맨몸, 바벨)

    @Column
    private Integer calories; // 소모 칼로리
}