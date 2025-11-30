package com.websever.websever.repository.health;

import com.websever.websever.entity.health.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Integer> {

    // 1. 카테고리별 운동 조회 (툴 지정 없이 전체 조회)
    List<ExerciseEntity> findByCategory(String category);

    // 2. 카테고리 + 도구별 운동 조회 (2단계 필터링)
    List<ExerciseEntity> findByCategoryAndTool(String category, String tool);

    // 3. 전체 운동 조회
    List<ExerciseEntity> findAllByOrderByNameAsc();
}