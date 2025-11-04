package com.websever.websever.service;

import com.websever.websever.entity.basicEntity;
import com.websever.websever.repository.basicRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class basicService {
    private final basicRepository basicRepository;

    public basicService(basicRepository basicRepository) {
        this.basicRepository = basicRepository;
    }

    @Transactional // 쓰기 작업은 @Transactional 별도 선언
    public basicEntity saveTestMessage(String message) {
        return basicRepository.save(new basicEntity(message));
    }

    public List<basicEntity> findAllMessages() {
        return basicRepository.findAll();
    }
}
