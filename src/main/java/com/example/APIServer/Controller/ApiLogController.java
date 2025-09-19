package com.example.APIServer.Controller;

import com.example.APIServer.Dto.ApiLogDto;
import com.example.APIServer.Entity.ApiLogEntity;
import com.example.APIServer.Service.ApiLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ApiLogController {
    private final ApiLogService apiLogService;

    @PostMapping
    public ResponseEntity<Void> receiveLog(@RequestBody ApiLogDto logDto) {
        apiLogService.createLog(logDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping // GET 요청을 처리합니다.
    public ResponseEntity<List<ApiLogEntity>> getLogs() {
        List<ApiLogEntity> logs = apiLogService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}
