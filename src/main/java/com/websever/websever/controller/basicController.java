package com.websever.websever.controller;

import com.websever.websever.entity.basicEntity;
import com.websever.websever.service.basicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class basicController {

    private final basicService basicService;

    public basicController(basicService basicService) {
        this.basicService = basicService;
    }

    @GetMapping("/api/testll")
    public List<basicEntity> runDbTestAndGetMessages() {
        basicService.saveTestMessage("데베 연결 굿: " + java.time.LocalDateTime.now());
        
        List<basicEntity> result = basicService.findAllMessages();
        
        return ResponseEntity.ok(result).getBody();
    }

    @GetMapping("/")
    public String home() {
        return "test 굿";
    }
}
