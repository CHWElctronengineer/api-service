package com.example.APIServer.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 처리
 */
@Component
public class JwtProvider {

    // JWT 비밀 키 주입
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT 만료 시간 주입
    @Value("${jwt.expiration}")
    private long validityInMs;

    // JWT 서명에 사용할 SecretKey
    private SecretKey signingKey;

    /**
     * SecretKey 초기화
     */
    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰 생성
     *
     * @param employeeId 사용자의 사원번호
     * @param role       사용자의 역할
     * @return 생성된 JWT 토큰
     */
    public String createToken(String employeeId, String role) {
        // JWT 페이로드(클레임) 설정
        Claims claims = Jwts.claims().setSubject(employeeId);
        claims.put("role", role);

        Date now = new Date();
        // 토큰 만료 시간 계산
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setClaims(claims) // 클레임 추가
                .setIssuedAt(now) // 토큰 발행 시간 설정
                .setExpiration(validity) // 토큰 만료 시간 설정
                .signWith(signingKey, SignatureAlgorithm.HS256) // 토큰 서명
                .compact(); // 토큰 문자열 반환
    }

    /**
     * JWT 유효성 검증
     *
     * @param token 검증할 JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * JWT에서 사원번호 추출
     *
     * @param token 사원번호를 추출할 JWT 토큰
     * @return 토큰에 저장된 사원번호(Subject)
     */
    public String getEmployeeId(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey)
                .build().parseClaimsJws(token)
                .getBody().getSubject(); // Subject 클레임 가져오기
    }

    /**
     * JWT에서 역할 추출
     *
     * @param token 역할을 추출할 JWT 토큰
     * @return 토큰에 저장된 역할(role)
     */
    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey)
                .build().parseClaimsJws(token)
                .getBody().get("role", String.class); // role 클레임 가져오기
    }
}