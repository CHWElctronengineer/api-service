package com.example.APIServer.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 데이터베이스의 'api_logs' 테이블과 직접 매핑되는 JPA 엔티티(Entity) 클래스입니다.
 * API 호출에 대한 로그 정보를 영구적으로 저장하는 데 사용됩니다.
 *
 * @Data: Lombok 어노테이션으로, getter, setter 등의 코드를 자동으로 생성합니다.
 * @Entity: 이 클래스가 JPA 엔티티임을 나타냅니다. JPA는 이 클래스를 기반으로 데이터베이스 작업을 수행합니다.
 * @Table(name = "api_logs"): 이 엔티티가 'api_logs'라는 이름의 데이터베이스 테이블에 매핑됨을 명시합니다.
 */
@Data
@Entity
@Builder // 빌더 추가
@AllArgsConstructor // 빌더 사용을 위한 전체 생성자 추가
@NoArgsConstructor  // JPA를 위한 기본 생성자 추가
@Table(name = "api_logs")
public class ApiLogEntity {

    /**
     * 로그의 고유 식별자 (Primary Key).
     * @Id: 이 필드가 테이블의 기본 키(PK)임을 나타냅니다.
     * @GeneratedValue(strategy = GenerationType.IDENTITY): 기본 키 생성을 데이터베이스에 위임합니다(예: MySQL의 AUTO_INCREMENT).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    /**
     * 로그가 생성된 시간.
     * @Column(columnDefinition = "..."): 데이터베이스 스키마 생성 시,
     * 이 컬럼의 기본값을 현재 시간으로 설정하고, 정밀도를 6자리(마이크로초)까지 지정합니다.
     * LocalDateTime.now(): JPA가 엔티티를 저장하기 직전, Java 객체에 현재 시간을 초기값으로 설정합니다.
     */
//    @Column(columnDefinition = "datetime default current_timestamp(6)")
//    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;

    /**
     * 요청을 처리한 서비스의 이름.
     */
    private String serviceName;

    /**
     * 호출된 API의 엔드포인트 URI.
     */
    private String apiEndpoint;

    /**
     * 요청에 사용된 HTTP 메소드 (e.g., "GET", "POST").
     */
    private String httpMethod;

    /**
     * API 요청 본문(payload)을 JSON 형태의 문자열로 저장합니다.
     * @Column(columnDefinition = "json"): 데이터베이스가 JSON 타입을 지원하는 경우,
     * 해당 컬럼을 JSON 타입으로 생성하여 더 효율적인 저장 및 조회를 가능하게 합니다.
     */
    @Column(columnDefinition = "json")
    private String requestPayload;

    /**
     * HTTP 응답 상태 코드.
     */
    private Integer responseStatus;

    /**
     * API 응답 본문(payload)을 문자열로 저장합니다.
     * @Column(columnDefinition = "text"): 일반적인 VARCHAR(255)보다 더 긴 텍스트를
     * 저장하기 위해 데이터베이스의 TEXT 타입으로 컬럼을 생성합니다.
     */
    @Column(columnDefinition = "text")
    private String responsePayload;

    /**
     * 요청을 보낸 클라이언트의 IP 주소.
     */
    private String clientIp;

    /**
     * 해당 요청-응답 사이클을 고유하게 식별하기 위한 추적 ID.
     */
    private String traceId;
}