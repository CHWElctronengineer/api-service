package com.example.APIServer.Dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder // 빌더 패턴 자동 생성, LogAspect.java에서 set을 남발하기 않기 위해 Builder를 사용함.
public class ApiLogDto {
    private String serviceName;
    private String apiEndpoint;
    private String httpMethod;
    private Object requestPayload; // 다양한 형태의 JSON을 받기 위해 Object 타입으로 설정
    private Integer responseStatus;
    private String responsePayload;
    private String clientIp;
    private String traceId;
}
