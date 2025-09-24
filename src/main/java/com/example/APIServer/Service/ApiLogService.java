package com.example.APIServer.Service;

import com.example.APIServer.Dto.ApiLogDto;
import com.example.APIServer.Entity.ApiLogEntity;
import com.example.APIServer.Repository.ApiLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional; // Spring의 @Transactional을 사용하는 것이 더 일반적입니다.
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort; // Sort import

import java.util.List;

/**
 * API 로그 데이터를 처리하는 비즈니스 로직을 담고 있는 서비스 클래스입니다.
 * @Service: 이 클래스가 비즈니스 로직을 담당하는 서비스 계층의 컴포넌트(Bean)임을 나타냅니다.
 * @RequiredArgsConstructor: final 필드에 대한 생성자를 자동으로 생성하여 의존성을 주입합니다.
 */
@Service
@RequiredArgsConstructor
public class ApiLogService {

    // 데이터베이스와 상호작용하기 위한 리포지토리
    private final ApiLogRepository apiLogRepository;
    // Java 객체를 JSON 문자열로 변환하기 위한 ObjectMapper
    private final ObjectMapper objectMapper;

    /**
     * ApiLogDto를 받아 ApiLogEntity로 변환한 후, 데이터베이스에 저장합니다.
     * @Transactional: 이 메소드의 모든 작업이 하나의 트랜잭션(Transaction)으로 처리되도록 보장합니다.
     * 작업 중 하나라도 실패하면, 모든 변경사항이 롤백(취소)됩니다.
     * @param dto LogAspect로부터 전달받은 로그 정보가 담긴 DTO
     */
    @Transactional
    public void createLog(ApiLogDto dto) {
        // DB에 저장하기 위한 엔티티 객체를 생성합니다.
        ApiLogEntity logEntity = ApiLogEntity.builder()
                .serviceName(dto.getServiceName())
                .apiEndpoint(dto.getApiEndpoint())
                .httpMethod(dto.getHttpMethod())
                .responseStatus(dto.getResponseStatus())
                .responsePayload(dto.getResponsePayload())
                .clientIp(dto.getClientIp())
                .traceId(dto.getTraceId())
                .build();

        try {
            // DTO의 requestPayload 필드는 다양한 객체 형태일 수 있으므로,
            // ObjectMapper를 사용해 JSON 형태의 문자열로 안전하게 변환합니다.
            if (dto.getRequestPayload() != null) {
                logEntity.setRequestPayload(objectMapper.writeValueAsString(dto.getRequestPayload()));
            }
        } catch (JsonProcessingException e) {
            // 만약 JSON 변환에 실패하면, 에러 로그를 기록합니다.
            logEntity.setRequestPayload("{\"error\":\"JSON_PARSING_FAILED\"}");
        }

        // 완성된 엔티티 객체를 리포지토리를 통해 데이터베이스에 저장합니다.
        apiLogRepository.save(logEntity);
    }

    /**
     * 데이터베이스에 저장된 모든 로그를 조회합니다.
     * @Transactional(readOnly = true)를 사용하면 읽기 전용으로 최적화할 수 있습니다.
     * @return ApiLogEntity 객체의 리스트
     */
    @Transactional
    public List<ApiLogEntity> getAllLogs() {
        // 단순히 모든 로그를 가져오는 것이 아니라, Sort 객체를 전달하여
        // createdAt 필드를 기준으로 '내림차순(최신순)'으로 정렬하여 반환합니다.
        return apiLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}