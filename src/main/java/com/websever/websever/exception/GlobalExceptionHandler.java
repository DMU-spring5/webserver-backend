package com.websever.websever.exception;

import com.websever.websever.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 아이디 중복, 비밀번호 불일치 등 (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        // 400 Bad Request 반환
        ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailure(RuntimeException e) {

        // 특정 메시지 ("인증된 사용자 정보를 찾을 수 없습니다.")를 확인하여 401로 처리
        if ("인증된 사용자 정보를 찾을 수 없습니다.".equals(e.getMessage())) {
            ErrorResponse errorResponse = new ErrorResponse("UNAUTHORIZED", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}