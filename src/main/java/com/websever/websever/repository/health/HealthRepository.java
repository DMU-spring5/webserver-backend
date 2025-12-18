package com.websever.websever.repository.health;

import com.websever.websever.entity.health.HealthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends JpaRepository<HealthEntity, Integer> {
}
