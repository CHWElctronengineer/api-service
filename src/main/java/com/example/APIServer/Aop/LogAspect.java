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

    // 모든 Controller 패키지의 클래스에 있는 모든 메서드에 대해 적용
    @Pointcut("execution(* com.example.APIServer.Controller.*Controller.*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString(); // 각 요청에 고유한 추적 ID 생성
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        ApiLogDto logDto = new ApiLogDto();
        logDto.setServiceName("APIServer");
        logDto.setApiEndpoint(request.getRequestURI());
        logDto.setHttpMethod(request.getMethod());
        logDto.setClientIp(request.getRemoteAddr());
        logDto.setTraceId(traceId);

        // 요청 페이로드 로깅
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Object) {
            logDto.setRequestPayload(objectMapper.convertValue(args[0], Object.class));
        }

        Object result = null;
        try {
            result = joinPoint.proceed(); // 실제 컨트롤러 메소드 실행
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                logDto.setResponseStatus(responseEntity.getStatusCode().value());
                logDto.setResponsePayload(objectMapper.writeValueAsString(responseEntity.getBody()));
            }
        } catch (Throwable e) {
            log.error("API Logging Error: {}", e.getMessage(), e);
            throw e; // 예외를 다시 던져서 기존 로직에 영향을 주지 않음
        } finally {
            // 로그 저장
            apiLogService.createLog(logDto);
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} {} - {}ms", traceId, request.getMethod(), request.getRequestURI(), endTime - startTime);
        }

        return result;
    }
}