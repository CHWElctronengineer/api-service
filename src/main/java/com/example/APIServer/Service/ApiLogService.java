package com.example.APIServer.Service;

import com.example.APIServer.Dto.ApiLogDto;
import com.example.APIServer.Entity.ApiLogEntity;
import com.example.APIServer.Repository.ApiLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiLogService {
    private final ApiLogRepository apiLogRepository;
    private final ObjectMapper objectMapper; // JSON 직렬화를 위해 주입

    @Transactional
    public void createLog(ApiLogDto dto) {
        ApiLogEntity logEntity = new ApiLogEntity();
        logEntity.setServiceName(dto.getServiceName());
        logEntity.setApiEndpoint(dto.getApiEndpoint());
        logEntity.setHttpMethod(dto.getHttpMethod());
        logEntity.setResponseStatus(dto.getResponseStatus());
        logEntity.setResponsePayload(dto.getResponsePayload());
        logEntity.setClientIp(dto.getClientIp());
        logEntity.setTraceId(dto.getTraceId());

        try {
            // DTO의 requestPayload 객체를 JSON 문자열로 변환하여 저장
            if (dto.getRequestPayload() != null) {
                logEntity.setRequestPayload(objectMapper.writeValueAsString(dto.getRequestPayload()));
            }
        } catch (JsonProcessingException e) {
            logEntity.setRequestPayload("{\"error\":\"JSON_PARSING_FAILED\"}");
        }

        apiLogRepository.save(logEntity);
    }

    // (추가) 로그를 검색하는 메소드 (Specification 사용)
    @Transactional
    public List<ApiLogEntity> getAllLogs() {
        // 모든 로그를 최신순으로 정렬하여 반환 (선택 사항)
        // Order by createdAt DESC
        return apiLogRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }
}