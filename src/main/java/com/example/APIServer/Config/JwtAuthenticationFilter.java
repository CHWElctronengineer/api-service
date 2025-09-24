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
 * Spring Security 필터 체인에서 JWT를 통해 인증을 처리하는 커스텀 필터입니다.
 * 모든 HTTP 요청에 대해 한 번씩 실행되며(@OncePerRequestFilter),
 * 요청 헤더에 포함된 JWT 토큰을 검증하고, 유효하다면 SecurityContext에 인증 정보를 설정합니다.
 * 이 필터를 거친 후, Spring Security는 설정된 인증 정보를 바탕으로 인가(Authorization) 처리를 수행합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // JWT 토큰의 생성 및 검증을 담당하는 컴포넌트

    /**
     * 실제 필터링 로직이 수행되는 메소드입니다.
     * @param request  들어온 HTTP 요청
     * @param response 앞으로 나갈 HTTP 응답
     * @param filterChain 다음 필터로 요청/응답을 전달하기 위한 객체
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- 1. HTTP 요청 헤더에서 JWT 토큰 추출 ---
        String bearerToken = request.getHeader("Authorization");
        String token = null;

        // 헤더가 존재하고 "Bearer "로 시작하는지 확인 (표준 JWT 인증 방식)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 접두사를 제거하여 순수한 토큰 값만 추출
            token = bearerToken.substring(7);
        }

        // --- 2. 토큰이 존재하고 유효한지 검증 ---
        if (token != null && jwtProvider.validateToken(token)) {

            // --- 3. 토큰에서 사용자 정보(사원번호, 역할) 추출 ---
            String employeeId = jwtProvider.getEmployeeId(token); // 토큰의 'subject' 클레임에서 사원번호를 가져옴
            String role = jwtProvider.getRole(token);           // 토큰의 'role' 커스텀 클레임에서 역할을 가져옴

            // --- 4. 추출한 역할이 유효한지 확인하고, Spring Security용 인증 객체 생성 ---
            if (role != null && isValidRole(role)) {
                // Spring Security가 이해할 수 있는 권한 형태로 변환 (ex: "ADMIN" -> "ROLE_ADMIN")
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // 인증된 사용자의 정보와 권한을 담는 Authentication 객체를 생성
                // new UsernamePasswordAuthenticationToken(principal, credentials, authorities)
                // principal: 인증된 사용자 식별자 (보통 ID나 User 객체)
                // credentials: 비밀번호 (JWT 인증에서는 사용하지 않으므로 null)
                // authorities: 사용자의 권한 목록
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        employeeId,
                        null,
                        Collections.singleton(authority) // 권한이 하나이므로 singleton 사용
                );

                // --- 5. SecurityContext에 Authentication 객체 저장 ---
                // SecurityContextHolder는 현재 요청에 대한 보안 컨텍스트를 담는 곳
                // 여기에 인증 정보를 저장하면, 해당 요청이 처리되는 동안 계속해서 인증된 상태로 간주됨
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 필터로 요청과 응답을 전달합니다.
        // 이 코드가 없으면 요청이 Controller까지 도달하지 않고 여기서 멈춥니다.
        filterChain.doFilter(request, response);
    }

    /**
     * 토큰에서 추출한 역할(role) 문자열이 시스템에서 정의한 유효한 역할인지 검사합니다.
     *
     * @param role 검사할 역할 문자열
     * @return 유효한 역할이면 true, 아니면 false
     */
    private boolean isValidRole(String role) {
        // 시스템에 정의된 역할들(ADMIN, WORKER, MANAGER)과 일치하는지 확인
        return role.equals("ADMIN") || role.equals("WORKER") || role.equals("MANAGER");
    }
}