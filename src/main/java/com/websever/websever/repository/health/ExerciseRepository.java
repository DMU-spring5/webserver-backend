package com.websever.websever.repository.health;

import com.websever.websever.entity.health.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Integer> {

    List<ExerciseEntity> findByCategory(String category);


    List<ExerciseEntity> findByCategoryAndTool(String category, String tool);

    // 3. 전체 운동 조회
    List<ExerciseEntity> findAllByOrderByNameAsc();

    List<ExerciseEntity> findByNameContainingIgnoreCase(String name);
}