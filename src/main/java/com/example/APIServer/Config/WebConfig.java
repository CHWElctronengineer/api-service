package com.example.APIServer.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // "/api/"로 시작하는 모든 경로에 대해
                .allowedOrigins("http://localhost:5174") // 리액트 앱 주소로부터의 요청을 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 모든 HTTP 헤더를 허용
                .allowCredentials(true); // 인증 정보(쿠키 등)를 포함한 요청 허용
    }
}
