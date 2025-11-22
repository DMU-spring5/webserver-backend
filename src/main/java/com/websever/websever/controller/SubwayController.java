package com.websever.websever.controller;

import com.websever.websever.dto.SubwayPathDto;
import com.websever.websever.service.TransportationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subway")
@RequiredArgsConstructor
public class SubwayController {

    private final TransportationService transportationService;

    // 호출 예시: http://localhost:9191/api/subway/path?cid=1000&sid=201&eid=222
    @GetMapping("/path")
    public ResponseEntity<?> getSubwayPath(
            @RequestParam String cid,
            @RequestParam String sid,
            @RequestParam String eid
    ) {
        if (cid == null || sid == null || eid == null) {
            return ResponseEntity.badRequest().body("필수 파라미터(cid, sid, eid)가 누락되었습니다.");
        }

        SubwayPathDto result = transportationService.getSubwayPath(cid, sid, eid);

        if (result == null || result.getResult() == null) {
            return ResponseEntity.status(500).body("경로를 찾을 수 없거나 API 호출에 실패했습니다.");
        }

        return ResponseEntity.ok(result);
    }
}