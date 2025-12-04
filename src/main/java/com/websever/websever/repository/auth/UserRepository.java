package com.websever.websever.repository.auth;

import com.websever.websever.entity.auth.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // 아이디 중복 확인 메소드
    boolean existsByUserId(String userId);
    Optional<UserEntity> findByUserId(String userId);

    //아이디를 찾기 위한 닉네임 검색 메소드
    Optional<UserEntity> findByNickname(String nickname);


}
