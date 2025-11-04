package com.websever.repository;

import com.websever.entity.basicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface basicRepository extends JpaRepository<basicEntity, Long> {
}
