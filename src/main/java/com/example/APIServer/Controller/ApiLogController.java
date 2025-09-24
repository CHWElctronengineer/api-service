package com.example.APIServer.Controller;

import com.example.APIServer.Dto.ApiLogDto;
import com.example.APIServer.Entity.ApiLogEntity;
import com.example.APIServer.Service.ApiLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 로그(log) 데이터를 생성하고 조회하는 RESTful API 엔드포인트를 제공하는 컨트롤러입니다.
 * '/api/logs' 경로에 대한 요청을 처리합니다.
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ApiLogController {

    /**
     * 로그 관련 비즈니스 로직을 처리하는 서비스입니다.
     * final 키워드와 @RequiredArgsConstructor를 통해 의존성이 주입됩니다.
     */
    private final ApiLogService apiLogService;

    /**
     * POST 요청을 통해 새로운 API 로그를 시스템에 기록합니다.
     * AOP Aspect(@LogAspect) 등 다른 서비스에서 이 엔드포인트를 호출하여 로그를 저장할 수 있습니다.
     *
     * @param logDto 클라이언트로부터 받은 로그 데이터가 담긴 DTO (Data Transfer Object)
     * @return HTTP 201 Created 상태 코드를 포함한 응답
     */
    @PostMapping
    public ResponseEntity<Void> receiveLog(@RequestBody ApiLogDto logDto) {
        // 서비스 레이어에 로그 생성을 위임합니다.
        apiLogService.createLog(logDto);
        // 성공적으로 리소스가 생성되었음을 알리는 201 상태 코드로 응답합니다.
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * GET 요청을 통해 시스템에 저장된 모든 API 로그 목록을 조회합니다.
     *
     * @return ApiLogEntity 객체의 리스트와 HTTP 200 OK 상태 코드를 포함한 응답
     */
    @GetMapping
    public ResponseEntity<List<ApiLogEntity>> getLogs() {
        // 서비스 레이어에서 모든 로그 데이터를 가져옵니다.
        List<ApiLogEntity> logs = apiLogService.getAllLogs();
        // 조회된 로그 목록을 응답 본문에 담아 200 상태 코드로 반환합니다.
        return ResponseEntity.ok(logs);
    }
}