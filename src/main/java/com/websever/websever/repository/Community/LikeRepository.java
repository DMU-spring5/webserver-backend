package com.websever.websever.repository.Community;

import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.community.LikeEntity;
import com.websever.websever.entity.community.postEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {
    // 특정 유저가 좋아요 누른 내역을 최신순으로 조회
    List<LikeEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    // 게시글별 좋아요 개수 조회
    Integer countByPost(postEntity post);
}