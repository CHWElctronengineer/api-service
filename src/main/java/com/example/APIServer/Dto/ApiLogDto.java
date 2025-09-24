package com.example.APIServer.Dto;


import lombok.Builder;
import lombok.Data;


/**
 * API 호출 로그 정보를 계층 간에 전달하기 위한 DTO(Data Transfer Object)입니다.
 * 이 DTO는 LogAspect에서 생성되어 ApiLogService로 전달되며,
 * 최종적으로 ApiLogEntity로 변환되어 데이터베이스에 저장됩니다.
 *
 * @Data: Lombok 어노테이션으로, getter, setter, toString, equals, hashCode 메소드를 자동으로 생성합니다.
 * @Builder: 빌더 패턴을 자동으로 생성하여, 객체 생성 시 가독성과 안정성을 높여줍니다.
 * LogAspect.java에서 setter 메소드를 여러 번 호출하는 대신 빌더를 사용하기 위해 추가되었습니다.
 */
@Data
@Builder
public class ApiLogDto {

    /**
     * 요청을 처리한 서비스의 이름 (e.g., "APIServer")
     */
    private String serviceName;  // 서비스 네임 어디에서 저장? 할당 하는지?

    /**
     * 호출된 API의 엔드포인트 URI (e.g., "/api/proxy/drone-images")
     */
    private String apiEndpoint;

    /**
     * 요청에 사용된 HTTP 메소드 (e.g., "GET", "POST")
     */
    private String httpMethod;

    /**
     * API 요청 본문(payload).
     * 다양한 형태의 JSON 데이터를 받기 위해 Object 타입으로 설정되었습니다.
     * DB 저장 시에는 JSON 문자열로 변환됩니다.
     */
    private Object requestPayload;

    /**
     * HTTP 응답 상태 코드 (e.g., 200, 404, 500)
     */
    private Integer responseStatus;

    /**
     * API 응답 본문(payload).
     * 이미지 같은 대용량 데이터의 경우, 전체 데이터 대신 요약 정보가 문자열로 저장될 수 있습니다.
     */
    private String responsePayload;

    /**
     * 요청을 보낸 클라이언트의 IP 주소
     */
    private String clientIp;

    /**
     * 해당 요청-응답 사이클을 고유하게 식별하기 위한 추적 ID (UUID)
     */
    private String traceId;
}