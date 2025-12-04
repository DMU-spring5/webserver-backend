package com.websever.websever.service.auth;

import com.websever.websever.dto.response.LoginResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // [회원가입]
    @Transactional
    public LoginResponse signup(UserEntity userEntity) {
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
        String userIdentifier = savedUser.getUserId();

        String accessToken = jwtTokenProvider.generateToken(userIdentifier);

        String refreshToken = jwtTokenProvider.generateRefreshToken(userIdentifier);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // [로그인]
    @Transactional(readOnly = true)
    public LoginResponse signIn(String userId, String rawPassword) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String userIdentifier = user.getUserId();

        String accessToken = jwtTokenProvider.generateToken(userIdentifier);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userIdentifier);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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

    // [비밀번호 찾기 - ]
    @Transactional
    public String resetAndRetrieveTempPassword(String nickname, String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!user.getNickname().equals(nickname)) {
            throw new IllegalArgumentException("닉네임 정보가 일치하지 않습니다.");
        }

        // 1. 임의의 6자리 숫자 생성
        SecureRandom random = new SecureRandom();
        int tempPasswordInt = 100000 + random.nextInt(900000); // 100000 ~ 999999
        String tempPassword = String.valueOf(tempPasswordInt); // 평문 임시 비밀번호

        // 2. 임시 비밀번호를 해시하여 DB에 저장 (업데이트)
        String hashedPassword = passwordEncoder.encode(tempPassword);
        user.setPassword(hashedPassword);


        return tempPassword;
    }
}