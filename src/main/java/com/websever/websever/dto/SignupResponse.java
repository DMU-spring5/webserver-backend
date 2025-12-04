//회원가입 성공 시 반환할 정보를 담음
package com.websever.websever.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class SignupResponse {
    private Integer id; //자동으로 생성된 db의 고유id
    private String userId; //사용자가 입력한 로그인id
    private String token; //회원가입과 동시에 발급되는 jwt토큰
}
