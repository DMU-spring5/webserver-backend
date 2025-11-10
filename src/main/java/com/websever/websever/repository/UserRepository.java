package com.websever.websever.repository;

import com.websever.websever.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // 아이디 중복 확인 메소드
    boolean existsByUserId(String userId);
    Optional<UserEntity> findByUserId(String userId);




}
