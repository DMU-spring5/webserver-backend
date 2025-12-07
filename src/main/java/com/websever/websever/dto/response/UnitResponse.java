package com.websever.websever.dto.response;

import com.websever.websever.entity.unit.UnitEntity;
import lombok.Data;

@Data
public class UnitResponse {
    private Integer userId;
    private Integer unitId;
    private String star;
    private String title;
    private String hard;
    private String working;
    private String good;
    private String bad;
    private String hope;

    public static UnitResponse create(UnitEntity entity) {
        UnitResponse response = new UnitResponse();
        response.setUnitId(entity.getUnitId());
        response.setUserId(entity.getUserId().getId()); // PK
        response.setStar(entity.getStar());
        response.setTitle(entity.getTitle());
        response.setHard(entity.getHard());
        response.setWorking(entity.getWorking());
        response.setGood(entity.getGood());
        response.setBad(entity.getBad());
        response.setHope(entity.getHope());
        return response;
    }
    }
