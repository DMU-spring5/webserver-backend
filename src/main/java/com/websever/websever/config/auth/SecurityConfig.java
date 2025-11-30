package com.websever.websever.config.auth;

import com.websever.websever.service.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // API 경로별 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/api/v1/community/create").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/community/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/community/**").authenticated()
                        .requestMatchers("/api/v1/transport/**").authenticated()
                        .requestMatchers("/api/v1/mainpage/**").authenticated()
                                .requestMatchers("/api/v1/health/**").authenticated()
                        .requestMatchers("/api/v1/mypage/**").authenticated()




                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}