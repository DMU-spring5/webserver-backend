package com.websever.websever.repository;

import com.websever.websever.entity.DataCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataCacheRepository extends JpaRepository<DataCacheEntity, Integer> {

    Optional<DataCacheEntity> findFirstByDataTypeOrderByFetchedAtDesc(String dataType);
}