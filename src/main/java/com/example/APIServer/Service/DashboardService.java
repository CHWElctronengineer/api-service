package com.example.APIServer.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Value("${erp.api.url:http://localhost:8081/api/projects}")
    private String ERP_PROJECTS_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> fetchProjectsFromErp() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    ERP_PROJECTS_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("ERP 데이터 호출 실패: " + e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }
}
