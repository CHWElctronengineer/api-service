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
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Spring AOP를 사용하여 Controller 계층의 모든 API 호출에 대한 로그를 자동으로 기록하는 클래스입니다.
 * @Aspect: 이 클래스가 Aspect(여러 객체에 공통으로 적용되는 기능)임을 나타냅니다.
 * @Component: Spring이 이 클래스를 Bean으로 관리하도록 합니다.
 * @Slf4j: Lombok을 통해 'log'라는 이름의 Logger 객체를 자동으로 생성합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    // final 필드들은 @RequiredArgsConstructor에 의해 생성자에서 자동으로 주입됩니다.
    private final ApiLogService apiLogService; // 로그를 DB에 저장하는 서비스
    private final ObjectMapper objectMapper;   // Java 객체를 JSON 문자열로 변환하기 위한 도구

    /**
     * 로그를 적용할 대상을 지정하는 Pointcut입니다.
     * execution(* com.example.APIServer.Controller.*Controller.*(..))
     * - execution(): 메소드 실행 조인 포인트를 지정합니다.
     * - *: 모든 반환 타입을 의미합니다.
     * - com.example.APIServer.Controller.*Controller: Controller 패키지 내에 이름이 'Controller'로 끝나는 모든 클래스를 대상으로 합니다.
     * - .*(..): 해당 클래스의 모든 메소드를 대상으로 하며, 파라미터는 상관하지 않습니다.
     */
    @Pointcut("execution(* com.example.APIServer.Controller.*Controller.*(..))")
    public void controllerMethods() {}

    /**
     * Pointcut으로 지정된 메소드의 실행 전, 후, 예외 발생 시점에 모두 개입하는 Around Advice입니다.
     * @param joinPoint 프록시 처리된 원본 메소드에 대한 정보를 담고 있습니다.
     * @return 원본 메소드가 반환하는 결과를 그대로 반환합니다.
     * @throws Throwable 원본 메소드에서 발생한 예외를 그대로 전파합니다.
     */
    @Around("controllerMethods()")
    public Object apiLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // 현재 HTTP 요청 정보를 가져옵니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 만약 요청 주소가 로그를 조회하는 API('/api/logs')라면, 무한 루프를 방지하기 위해 로깅을 건너뜁니다.
        if (request.getRequestURI().startsWith("/api/logs")) {
            return joinPoint.proceed(); // 원본 메소드를 그대로 실행하고 즉시 반환합니다.
        }

        // 요청 처리 시작 시간을 기록합니다.
        long startTime = System.currentTimeMillis();
        // 각 요청을 고유하게 식별하기 위한 추적 ID를 생성합니다.
        String traceId = UUID.randomUUID().toString();

        // 요청 URI를 기반으로 서비스 이름을 결정하는 로직
        String serviceName = "APIServer"; // 기본값
        String uri = request.getRequestURI();

        if (uri.contains("/proxy/drone-images")) {
            serviceName = "Drone Server";
        } else if (uri.contains("/proxy/employees") || uri.contains("/proxy/sales_orders")
                || uri.contains("/proxy/project_plans") || uri.contains("/api/proxy/sales-orders")
                || uri .contains("/api/proxy/positions") || uri.contains("/api/proxy/inventory")) {
            serviceName = "ERP Server";
        } else if (uri.contains("/proxy/shipments") ) {
            serviceName = "MES Server";
        }

        ApiLogDto logDto = ApiLogDto.builder()
                .serviceName(serviceName) // "APIServer" 대신 serviceName 변수 사용
                .apiEndpoint(request.getRequestURI())
                .httpMethod(request.getMethod())
                .clientIp(request.getRemoteAddr())
                .traceId(traceId)
                .build();

        // Controller 메소드로 전달된 파라미터(Request Body 등)를 로깅합니다.
        Object[] args = joinPoint.getArgs();
        // 파라미터가 존재하고 null이 아닐 경우
        if (args.length > 0 && args[0] != null) {
            // 파라미터가 MultipartFile 타입인지 먼저 확인합니다.
            if (args[0] instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) args[0];
                // 파일인 경우, 파일의 요약 정보만 기록합니다.
                String payloadSummary = "{ \"fileName\": \"" + file.getOriginalFilename() + "\", \"size\": " + file.getSize() + " }";
                logDto.setRequestPayload(payloadSummary);
            } else {  // 파일이 아닌 다른 모든 데이터는 기존처럼 JSON으로 변환합니다.

                try {
                    // 첫 번째 파라미터를 JSON 문자열로 변환하여 저장합니다.
                    logDto.setRequestPayload(objectMapper.writeValueAsString(args[0]));
                } catch (Exception e) {
                    logDto.setRequestPayload("Request payload logging error: " + e.getMessage());
                }
            }
        }

        Object result = null;
        try {
            // === 여기서 실제 Controller 메소드가 실행됩니다. ===
            result = joinPoint.proceed();
            // =================================================

            // Controller 메소드 실행 후, 응답(result)을 로깅합니다.
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                logDto.setResponseStatus(responseEntity.getStatusCode().value());

                // 응답 본문이 byte 배열(이미지 데이터)인지 확인합니다.
                if (responseEntity.getBody() instanceof byte[]) {
                    byte[] responseBytes = (byte[]) responseEntity.getBody();
                    // 이미지 데이터인 경우, DB에 저장하기에는 너무 크므로 요약 정보만 기록합니다.
                    logDto.setResponsePayload("Image byte data (size: " + responseBytes.length + " bytes)");
                } else if (responseEntity.getBody() != null) {
                    // 다른 타입(JSON 등)의 응답은 전체 내용을 JSON 문자열로 기록합니다.
                    logDto.setResponsePayload(objectMapper.writeValueAsString(responseEntity.getBody()));
                }
            } else { // Controller가 ResponseEntity가 아닌 일반 객체를 반환한 경우
                logDto.setResponseStatus(200); // 성공(200 OK)으로 간주합니다.
                if (result != null) {
                    logDto.setResponsePayload(objectMapper.writeValueAsString(result));
                }
            }
        } catch (Throwable e) {
            // Controller 메소드 실행 중 예외가 발생한 경우
            log.error("API Logging Error: {}", e.getMessage(), e);
            logDto.setResponseStatus(500); // 상태 코드를 500(Internal Server Error)으로 기록합니다.
            logDto.setResponsePayload("{\"error\": \"" + e.getMessage() + "\"}");
            throw e; // 예외를 다시 던져서 정상적인 예외 처리가 이뤄지도록 합니다.
        } finally {
            // 메소드가 성공적으로 끝나든, 예외가 발생하든 항상 실행되는 블록입니다.

            // 완성된 로그 DTO를 DB에 저장합니다.
            apiLogService.createLog(logDto);

            // 요청 처리 종료 시간을 기록하고, 총 소요 시간을 계산하여 콘솔에 로그를 출력합니다.
            long endTime = System.currentTimeMillis();
            log.info("[{}] {} {} - {}ms", traceId, request.getMethod(), request.getRequestURI(), endTime - startTime);
        }

        // Controller가 반환한 원래 결과를 클라이언트에게 그대로 반환합니다.
        return result;
    }
}