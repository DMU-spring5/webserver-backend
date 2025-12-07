package com.websever.websever.controller.unit;

import com.websever.websever.dto.request.UnitRequest;
import com.websever.websever.dto.response.UnitResponse;
import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.service.unit.UnitService;
import com.websever.websever.service.mainpage.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unit")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UnitResponse> createUnit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UnitRequest request) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        return ResponseEntity.ok(
                unitService.create(userInfo.getUserId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        List<UnitResponse> response = unitService.getAllUnits();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{unitId}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Integer unitId) {
        UnitResponse response = unitService.getUnitById(unitId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<UnitResponse> updateUnit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer unitId,
            @RequestBody UnitRequest request) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        UnitResponse response = unitService.updateUnit(unitId, currentUsername, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> deleteUnit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer unitId) {

        String currentUsername = userDetails.getUsername();
        UserResponse userInfo = userService.getCurrentUserInfo(currentUsername);

        unitService.deleteUnit(unitId, currentUsername);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UnitResponse>> searchUnits(
            @RequestParam String keyword) {

        List<UnitResponse> response = unitService.searchByTitle(keyword);
        return ResponseEntity.ok(response);
    }
}
