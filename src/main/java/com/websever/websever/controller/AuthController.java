//api,프론트엔드의 요청 수락
package com.websever.websever.controller;

import com.websever.websever.dto.LoginRequest;
import com.websever.websever.dto.FindIdRequest;
import com.websever.websever.dto.FindPasswordRequest;
import com.websever.websever.entity.UserEntity;
import com.websever.websever.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserEntity userEntity) {
        try {
            authService.signup(userEntity);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    //로그인 api추가
    @PostMapping("/login")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
        try {
            UserEntity authenticatedUser = authService.signIn(
                    loginRequest.getUserId(),
                    loginRequest.getPassword()
            );

            // TODO: 여기서 JWT 토큰을 생성 및 반환.
            String successMessage = authenticatedUser.getUserId() + "님, 로그인에 성공했습니다.";
            return ResponseEntity.ok(successMessage);

        } catch (IllegalArgumentException e) {
            // 아이디 또는 비밀번호 불일치
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 아이디 찾기 API
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody FindIdRequest findIdRequest) {
        try {
            String userId = authService.findUserId(
                    findIdRequest.getNickname(),
                    findIdRequest.getPassword()
            );

            String successMessage = "회원님의 아이디는 " + userId + " 입니다.";
            return ResponseEntity.ok(successMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    //비밀번호 찾기 API
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        try {
            boolean isUserVerified = authService.verifyUserForPasswordReset(
                    findPasswordRequest.getNickname(),
                    findPasswordRequest.getUserId()
            );

            if (isUserVerified) {

                return ResponseEntity.ok("사용자 정보가 확인되었습니다. (비밀번호 변경 페이지로 이동)");
            } else {
                return ResponseEntity.badRequest().body("사용자 정보가 일치하지 않습니다.");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }



}