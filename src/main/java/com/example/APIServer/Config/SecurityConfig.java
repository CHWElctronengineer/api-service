package com.example.APIServer.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security를 사용하여 웹 보안을 설정하는 클래스입니다.
 * @Configuration: 이 클래스가 Spring의 설정 정보를 담고 있음을 나타냅니다.
 * @EnableWebSecurity: Spring Security의 웹 보안 기능을 활성화합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 애플리케이션의 주 보안 규칙을 정의하는 필터 체인을 Bean으로 등록합니다.
     * 모든 HTTP 요청은 이 필터 체인에 정의된 규칙을 통과해야 합니다.
     * @param http HttpSecurity 객체를 통해 보안 규칙을 설정합니다.
     * @return 빌드된 SecurityFilterChain 객체
     * @throws Exception 설정 과정에서 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화합니다.
                // JWT 같은 토큰 기반 인증을 사용하는 stateless API에서는 일반적으로 비활성화합니다.
                .csrf(AbstractHttpConfigurer::disable)

                // CORS(Cross-Origin Resource Sharing) 설정을 활성화하고,
                // 아래에 정의된 corsConfigurationSource Bean을 사용하도록 지정합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // HTTP 요청에 대한 인가(Authorization) 규칙을 설정합니다.
                .authorizeHttpRequests(authorize -> authorize
                        // "/api/proxy/" 또는 "/api/logs"로 시작하는 경로의 모든 요청은
                        // 인증(로그인) 없이 누구나 접근할 수 있도록 허용(permitAll)합니다.
                        .requestMatchers("/api/proxy/**", "/api/logs").permitAll()
                        // "/api/proxy/**" : 하위 경로 허용.
                        // "/api/logs" : 정확히 일치해야 함. 하위 경로 미허용.
                        // 위에서 지정한 경로 외의 모든 요청은 반드시 인증이 필요함을 명시합니다.
                        .anyRequest().authenticated()
                );

        // 설정된 규칙에 따라 SecurityFilterChain 객체를 생성하여 반환합니다.
        return http.build();
    }

    /**
     * CORS 정책을 정의하는 Bean입니다.
     * 다른 출처(Origin)의 프론트엔드 애플리케이션(예: 리액트 앱)이
     * 이 서버의 API를 호출할 수 있도록 허용하는 규칙을 설정합니다.
     * @return CORS 설정 소스 객체
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS 설정 객체를 생성합니다.
        CorsConfiguration config = new CorsConfiguration();

        // 요청을 허용할 출처(프론트엔드 서버 주소) 목록을 지정합니다.
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:5176"));

        // 허용할 HTTP 메소드 목록을 지정합니다.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 HTTP 헤더 목록을 지정합니다. ("*"는 모든 헤더를 허용)
        config.setAllowedHeaders(List.of("*"));

        // 자격 증명(쿠키, 인증 헤더 등)을 포함한 요청을 허용할지 여부를 설정합니다.
        config.setAllowCredentials(true);

        // URL 기반의 CORS 설정 소스를 생성합니다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 설정을 적용합니다.
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}