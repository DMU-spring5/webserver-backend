package com.websever.websever.service.mypage;

import com.websever.websever.dto.response.NewsResponse;
import com.websever.websever.entity.news.RecommendEntity;
import com.websever.websever.repository.news.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageNewsService {

    private final RecommendRepository recommendRepository;

    /**
     * 마이페이지 > 스크랩한 뉴스 목록 조회
     */
    @Transactional(readOnly = true)
    public List<NewsResponse> getScrappedNews(String userId) {
        // liked가 'Y'인 기록만 조회
        List<RecommendEntity> scraps = recommendRepository.findByUser_UserIdAndLikedOrderByRecommendedAtDesc(userId, "Y");

        return scraps.stream()
                .map(recommend -> NewsResponse.from(recommend.getNews()))
                .collect(Collectors.toList());
    }
}