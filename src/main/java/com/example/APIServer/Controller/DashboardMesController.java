package com.example.APIServer.Controller;

import com.example.APIServer.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proxy/dashboard")
@RequiredArgsConstructor
public class DashboardMesController {

    private final DashboardService dashboardService;

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getErpProjects() {
        return ResponseEntity.ok(dashboardService.fetchProjectsFromErp());
    }

}
