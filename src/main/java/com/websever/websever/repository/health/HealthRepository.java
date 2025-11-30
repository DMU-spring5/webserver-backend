package com.websever.websever.repository.health;

import com.websever.websever.entity.health.HealthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends JpaRepository<HealthEntity, Integer> {
    // 필요 시 사용자의 날짜별 운동 기록 조회 등의 메서드 추가 가능
}
