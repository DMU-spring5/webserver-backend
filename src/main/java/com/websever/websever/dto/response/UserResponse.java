package com.websever.websever.dto.response;

import com.websever.websever.entity.auth.ServiceType;
import com.websever.websever.entity.auth.UserEntity;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class UserResponse {
    private final Integer id;
    private final String userId;
    private final String nickname;
    private final ServiceType serviceType;
    private final String division;
    private final String unit;
    private final LocalDate enlistDate;

public UserResponse(UserEntity user) {
    this.id = user.getId();
    this.userId = user.getUserId();
    this.nickname = user.getNickname();
    this.serviceType = user.getServiceType();
    this.division = user.getDivision();
    this.unit = user.getUnit();
    this.enlistDate = user.getEnlistDate();
}
}