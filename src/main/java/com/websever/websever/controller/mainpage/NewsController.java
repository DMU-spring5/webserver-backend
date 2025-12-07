package com.websever.websever.controller.mainpage;

import com.websever.websever.service.mainpage.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/naver")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    public ResponseEntity<String> searchNews(@RequestParam String query) {
        return ResponseEntity.ok(newsService.searchNews(query));
    }
}
