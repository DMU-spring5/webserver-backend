package com.websever.websever.repository.news;

import com.websever.websever.entity.news.RecommendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<RecommendEntity, Integer> {

    // 특정 유저가 '좋아요(liked=Y)' 표시한 뉴스 목록 조회
    // 최신 스크랩 순으로 정렬 (OrderByRecommendedAtDesc)
    List<RecommendEntity> findByUser_UserIdAndLikedOrderByRecommendedAtDesc(String userId, String liked);
}