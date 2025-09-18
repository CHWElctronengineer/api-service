package com.example.APIServer.Config;

import com.example.APIServer.Config.JwtAuthenticationFilter;

import com.example.APIServer.Service.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        System.out.println(">>>>>>>>>> SecurityConfig가 성공적으로 로드되었습니다! <<<<<<<<<<");
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf ->csrf.disable())

                // ★★★ 1. 기본 폼 로그인 비활성화 ★★★
                .formLogin(AbstractHttpConfigurer::disable)
                // ★★★ 2. HTTP Basic 인증 비활성화 ★★★
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(
                        sess ->sess.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(
                        auth -> auth
                                // ★★★ 수정된 부분 ★★★
                                // /api/로 시작하는 모든 경로는 인증이 필요하도록 설정
                                .requestMatchers("/api/**").authenticated()
                                // 그 외의 모든 요청은 일단 허용 (필요에 따라 변경)
                                .anyRequest().permitAll()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    // ★★★ PasswordEncoder Bean은 삭제 ★★★
}