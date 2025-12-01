package com.websever.websever.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword; // 기존 비밀번호
    private String newPassword;     // 새 비밀번호
}