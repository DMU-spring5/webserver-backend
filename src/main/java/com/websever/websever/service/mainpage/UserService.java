package com.websever.websever.service.mainpage;

import com.websever.websever.dto.response.UserResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.repository.auth.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserInfo(String userId) {

        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        return new UserResponse(userEntity);
    }
}
