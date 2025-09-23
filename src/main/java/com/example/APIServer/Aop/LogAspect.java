package com.example.APIServer.Aop;

import com.example.APIServer.Dto.ApiLogDto;
import com.example.APIServer.Service.ApiLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    private final ApiLogService apiLogService;
    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.example.APIServer.Controller.*Controller.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (request.getRequestURI().startsWith("/api/logs")) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString();

        ApiLogDto logDto = new ApiLogDto();
        logDto.setServiceName("APIServer");
        logDto.setApiEndpoint(request.getRequestURI());
        logDto.setHttpMethod(request.getMethod());
        logDto.setClientIp(request.getRemoteAddr());
        logDto.setTraceId(traceId);

        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            try {
                logDto.setRequestPayload(objectMapper.writeValueAsString(args[0]));
            } catch (Exception e) {
                logDto.setRequestPayload("Request payload logging error: " + e.getMessage());
            }
        }

        Object result = null;
        try {
            result = joinPoint.proceed();

            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                logDto.setResponseStatus(responseEntity.getStatusCode().value());

                // ✅ [수정] 응답 본문이 byte 배열(이미지 데이터)인지 확인
                if (responseEntity.getBody() instanceof byte[]) {
                    byte[] responseBytes = (byte[]) responseEntity.getBody();
                    // ✅ 이미지 데이터인 경우, 전체 내용을 저장하는 대신 간단한 요약 메시지를 저장
                    logDto.setResponsePayload("Image byte data (size: " + responseBytes.length + " bytes)");
                } else if (responseEntity.getBody() != null) {
                    // ✅ 다른 타입(JSON 등)의 응답은 기존처럼 전체 내용을 기록
                    logDto.setResponsePayload(objectMapper.writeValueAsString(responseEntity.getBody()));
                }
            } else {
                logDto.setResponseStatus(200);
                if (result != null) {
                    logDto.setResponsePayload(objectMapper.writeValueAsString(result));
                }
            }
        } catch (Throwable e) {
            log.error("API Logging Error: {}", e.getMessage(), e);
            logDto.setResponseStatus(500);
            logDto.setResponsePayload("{\"error\": \"" + e.getMessage() + "\"}");
            throw e;
        } finally {
            apiLogService.createLog(logDto);
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} {} - {}ms", traceId, request.getMethod(), request.getRequestURI(), endTime - startTime);
        }

        return result;
    }
}