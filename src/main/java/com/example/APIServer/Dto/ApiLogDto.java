package com.example.APIServer.Dto;

import lombok.Data;

@Data
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
