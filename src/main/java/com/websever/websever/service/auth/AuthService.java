package com.websever.websever.service.auth;

import com.websever.websever.dto.response.SignupResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // [회원가입]
    @Transactional
    public SignupResponse signup(UserEntity userEntity) {
        if (userRepository.existsByUserId(userEntity.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        if (userEntity.getImgUrl() == null || userEntity.getImgUrl().isEmpty()) {
            userEntity.setImgUrl("default_url"); // 기본 URL
        }

        userEntity.setServiceAgreed("Y");
        userEntity.setLocationAgreed("Y");

        UserEntity savedUser = userRepository.save(userEntity);
        String token = jwtTokenProvider.generateToken(savedUser.getUserId());

        return SignupResponse.builder()
                .id(savedUser.getId())
                .userId(savedUser.getUserId())
                .token(token)
                .build();
    }

    // [로그인]
    @Transactional(readOnly = true)
    public String signIn(String userId, String rawPassword) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenProvider.generateToken(user.getUserId());
    }

    // [아이디 찾기]
    @Transactional(readOnly = true)
    public String findUserId(String nickname, String rawPassword) {
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 닉네임입니다."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user.getUserId();
    }

    // [비밀번호 찾기 - 검증]
    @Transactional(readOnly = true)
    public boolean verifyUserForPasswordReset(String nickname, String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!user.getNickname().equals(nickname)) {
            throw new IllegalArgumentException("닉네임 정보가 일치하지 않습니다.");
        }
        return true;
    }
}