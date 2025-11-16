package com.websever.websever.controller.auth;

import com.websever.websever.dto.request.FindIdRequest;
import com.websever.websever.dto.request.FindPasswordRequest;
import com.websever.websever.dto.request.LoginRequest;
import com.websever.websever.dto.response.LoginResponse;
import com.websever.websever.dto.response.SignupResponse;
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
    public ResponseEntity<SignupResponse> signup(@RequestBody UserEntity userEntity) {
        SignupResponse signupResponse = authService.signup(userEntity);
        return ResponseEntity.ok(signupResponse);
    }

    // [로그인]
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest loginRequest) {
        String token = authService.signIn(
                loginRequest.getUserId(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(new LoginResponse(token));
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

    // [비밀번호 찾기 - 검증]
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        authService.verifyUserForPasswordReset(
                findPasswordRequest.getNickname(),
                findPasswordRequest.getUserId()
        );
        return ResponseEntity.ok("사용자 정보가 확인되었습니다. (비밀번호 재설정 단계로 이동)");
    }
}