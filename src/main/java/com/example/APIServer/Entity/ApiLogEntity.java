package com.example.APIServer.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "api_logs")
public class ApiLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(columnDefinition = "datetime default current_timestamp(6)")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String serviceName;
    private String apiEndpoint;
    private String httpMethod;

    @Column(columnDefinition = "json")
    private String requestPayload; // JSON 데이터를 문자열로 저장

    private Integer responseStatus;
    @Column(columnDefinition = "text")
    private String responsePayload;

    private String clientIp;
    private String traceId;
}