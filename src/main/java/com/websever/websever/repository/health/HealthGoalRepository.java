package com.websever.websever.repository.health;

import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.health.HealthGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthGoalRepository extends JpaRepository<HealthGoalEntity, Integer> {
    // 사용자의 가장 최근 목표 정보 조회
    Optional<HealthGoalEntity> findTopByUserOrderByCreatedAtDesc(UserEntity user);
}