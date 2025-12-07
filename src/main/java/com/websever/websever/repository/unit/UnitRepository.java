package com.websever.websever.repository.unit;

import com.websever.websever.entity.unit.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<UnitEntity, Integer> {

    List<UnitEntity> findByTitleContainingIgnoreCase(String title);
}
