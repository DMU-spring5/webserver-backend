package com.websever.websever.controller.auth;

import com.websever.websever.dto.request.FindIdRequest;
import com.websever.websever.dto.request.FindPasswordRequest;
import com.websever.websever.dto.request.LoginRequest;
import com.websever.websever.dto.response.LoginResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.service.auth.AuthService;
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

    // [회원가입]
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody UserEntity userEntity) {
        LoginResponse signupResponse = authService.signup(userEntity);
        return ResponseEntity.ok(signupResponse);
    }

    // [로그인]
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.signIn(
                loginRequest.getUserId(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(loginResponse);
    }

    // [아이디 찾기]
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody FindIdRequest findIdRequest) {
        String userId = authService.findUserId(
                findIdRequest.getNickname(),
                findIdRequest.getPassword()
        );
        String successMessage = "회원님의 아이디는 " + userId + " 입니다.";
        return ResponseEntity.ok(successMessage);
    }

    // [비밀번호 찾기 - 임시 비밀번호 발급]
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        // 임시 비밀번호를 생성하고 DB를 업데이트한 후, 평문 임시 비밀번호를 반환
        String tempPassword = authService.resetAndRetrieveTempPassword(
                findPasswordRequest.getNickname(),
                findPasswordRequest.getUserId()
        );

        String responseMessage = "새로 발급받은 비밀번호는 " + tempPassword + " 입니다.";
        return ResponseEntity.ok(responseMessage);
    }
}