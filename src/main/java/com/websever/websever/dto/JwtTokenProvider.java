package com.websever.websever.dto;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

}
