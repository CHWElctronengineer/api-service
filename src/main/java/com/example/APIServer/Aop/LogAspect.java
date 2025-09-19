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

    // A pointcut that targets all methods in any class with 'Controller' in its name
    @Pointcut("execution(* com.example.APIServer.Controller.*Controller.*(..))")
    public void controllerMethods() {}

    // Advice that runs around the execution of the methods specified by the pointcut
    @Around("controllerMethods()")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // Get the HttpServletRequest object from the current request context
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // If the request URI is for the logs endpoint, proceed without logging
        if (request.getRequestURI().startsWith("/api/logs")) {
            return joinPoint.proceed();
        }

        // The following logic will only execute for requests that are NOT for /api/logs
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString();

        ApiLogDto logDto = new ApiLogDto();
        logDto.setServiceName("APIServer");
        logDto.setApiEndpoint(request.getRequestURI());
        logDto.setHttpMethod(request.getMethod());
        logDto.setClientIp(request.getRemoteAddr());
        logDto.setTraceId(traceId);

        // Capture and log the request payload if it exists
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Object) {
            logDto.setRequestPayload(objectMapper.convertValue(args[0], Object.class));
        }

        Object result = null;
        try {
            // Proceed with the actual controller method execution
            result = joinPoint.proceed();

            // Capture the response status and payload
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                logDto.setResponseStatus(responseEntity.getStatusCode().value());
                logDto.setResponsePayload(objectMapper.writeValueAsString(responseEntity.getBody()));
            } else {
                // If it's not a ResponseEntity, assume a 200 OK status and capture the body
                logDto.setResponseStatus(200);
                if (result != null) {
                    logDto.setResponsePayload(objectMapper.writeValueAsString(result));
                }
            }
        } catch (Throwable e) {
            log.error("API Logging Error: {}", e.getMessage(), e);
            // On exception, record a 500 status and re-throw the exception
            logDto.setResponseStatus(500);
            logDto.setResponsePayload("{\"error\": \"" + e.getMessage() + "\"}");
            throw e;
        } finally {
            // Save the log entry to the database
            apiLogService.createLog(logDto);
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} {} - {}ms", traceId, request.getMethod(), request.getRequestURI(), endTime - startTime);
        }

        return result;
    }
}