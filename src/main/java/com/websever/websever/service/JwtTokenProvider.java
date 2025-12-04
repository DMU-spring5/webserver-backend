package com.websever.websever.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenProvider {
    private final SecretKey key;
    //토큰 유효 시간:1시간
    private final long validityInMilliseconds= 3600000;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key= Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    }
    //사용자id를 기반으로 jwt토큰을 생성
    public String generateToken(String userId) {
        Date now = new Date();
        Date validity= new Date(now.getTime() + validityInMilliseconds);

        return Jwts.bulider()
                .subject(userId) //사용자 id
                .issuedAt(now) //발급 시간
                .expiration(validity) //만료 시간
                .signWith(key)
                .compact(); //문자열로 반환
    }

}
