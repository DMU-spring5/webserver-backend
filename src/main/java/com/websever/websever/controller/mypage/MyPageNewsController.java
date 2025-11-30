package com.websever.websever.controller.mypage;

import com.websever.websever.dto.response.NewsResponse;
import com.websever.websever.service.mypage.MyPageNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage/news")
@RequiredArgsConstructor
public class MyPageNewsController {

    private final MyPageNewsService myPageNewsService;

    /**
     * 스크랩(좋아요)한 뉴스 조회 API
     * GET /api/v1/mypage/news/scrap
     */
    @GetMapping("/scrap")
    public ResponseEntity<List<NewsResponse>> getScrappedNews(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        List<NewsResponse> response = myPageNewsService.getScrappedNews(userId);

        return ResponseEntity.ok(response);
    }
}