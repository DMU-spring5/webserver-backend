package com.websever.websever.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 지하철역 정보를 찾지 못했을 때 발생하는 예외
 * HTTP 상태 코드 404 (Not Found)를 반환하도록 설정
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 500 대신 404를 반환하도록 지정
public class StationNotFoundException extends RuntimeException {

    public StationNotFoundException(String message) {
        super(message);
    }

    public StationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}