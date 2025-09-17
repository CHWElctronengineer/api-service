package com.example.APIServer.Config;

import com.example.APIServer.Service.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT를 통해 인증을 처리하는 커스텀 필터.
 * 모든 요청에 대해 JWT 토큰을 검증하고, 유효하면 SecurityContext에 인증 정보를 설정.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 JWT 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        // 2. 토큰이 유효한지 검증
        if (token != null && jwtProvider.validateToken(token)) {
            // 3. 토큰에서 사용자 정보(사원번호, 역할) 추출
            String employeeId = jwtProvider.getEmployeeId(token);
            String role = jwtProvider.getRole(token);

            // 4. 추출한 역할이 유효한지 확인하고, 인증 객체 생성
            if (role != null && isValidRole(role)) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        employeeId,
                        null,
                        Collections.singleton(authority)
                );

                // 5. SecurityContext에 Authentication 객체 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰에서 추출한 역할이 유효한지 검사.
     *
     * @param role 검사할 역할 문자열
     * @return 유효성 여부
     */
    private boolean isValidRole(String role) {
        return role.equals("ADMIN") || role.equals("WORKER") || role.equals("MANAGER");
    }
}