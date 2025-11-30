package com.websever.websever.entity.health;

import com.websever.websever.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "health")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_id")
    private Integer healthId;

    // 운동을 기록한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private UserEntity user;

    // 수행한 운동
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEntity exercise;

    @Column(name = "duration_min", nullable = false)
    private Integer durationMin; // 운동 시간(분)

    @Column(name = "total_calories", nullable = false)
    private Integer totalCalories; // 총 소모 칼로리

    @Column
    private LocalDate date; // 운동 날짜
}