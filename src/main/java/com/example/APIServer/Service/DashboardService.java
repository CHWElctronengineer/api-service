package com.example.APIServer.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {
    @Value("${erp.api.url:http://localhost:8081/api/projects}")
    private String ERP_PROJECTS_URL;

    @Value("${erp.api.customers.url:http://localhost:8081/api/customers}")
    private String ERP_CUSTOMERS_URL;

    @Value("${erp.api.customers.url:http://localhost:8081/api/}")
    private String ERP_BASE_URL;

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

    //고객사
    public List<Map<String, Object>> fetchCustomersFromErp() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    ERP_CUSTOMERS_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("⚠ ERP 고객사 데이터 호출 실패: " + e.getMessage());
            return List.of();
        }
    }

    //자재
    public List<Map<String, Object>> fetchMaterialsFromErp() {
        String url = ERP_BASE_URL + "materials";
        ResponseEntity<List<Map<String, Object>>> res = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {}
        );
        return Optional.ofNullable(res.getBody()).orElse(List.of());
    }

    public List<Map<String, Object>> fetchPurchaseOrdersFromErp() {
        String url = ERP_BASE_URL + "purchaseOrders";
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("ERP 구매 데이터 호출 실패: " + e.getMessage());
            return List.of();
        }
    }

    //생산계획
    public List<Map<String, Object>> fetchProjectPlansFromErp() {
        String url = ERP_BASE_URL + "project_plans";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> plans = response.getBody();
            System.out.println("✅ ERP 생산계획 데이터 수신 완료 (" +
                    (plans != null ? plans.size() : 0) + "건)");

            return Optional.ofNullable(plans).orElse(List.of());

        } catch (Exception e) {
            System.err.println("ERP 생산계획 데이터 호출 실패: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> fetchSalesOrdersFromErp() {
        String url = ERP_BASE_URL + "sales_orders";
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    // 7. 인사 현황 가져오기
    public List<Map<String, Object>> fetchEmployeesFromErp() {
        String url = ERP_BASE_URL + "employees";
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

}
