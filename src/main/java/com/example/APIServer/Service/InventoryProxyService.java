// src/main/java/com/example/APIServer/service/InventoryProxyService.java
package com.example.APIServer.Service;

import com.example.APIServer.Dto.StockRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class InventoryProxyService {

    private final RestTemplate restTemplate;

    // ERP 서버 base URL
    private static final String ERP_BASE_URL = "http://localhost:8081/api/inventory";

    /** ERP 전체 재고 조회 */
    public Object getAllInventories() {
        return restTemplate.getForObject(ERP_BASE_URL, Object.class);
    }

    /** 재고 차감 */
    public void deductStock(StockRequestDto request) {
        restTemplate.postForEntity(ERP_BASE_URL + "/deduct", request, Void.class);
    }

    /** 재고 복구 */
//    public void restoreStock(StockRequestDto request) {
//        restTemplate.postForEntity(ERP_BASE_URL + "/restore", request, Void.class);
//    }

    /** 재고 복구 */
    public void restoreStock(StockRequestDto request, HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 프론트에서 들어온 Authorization 헤더를 그대로 ERP에 전달
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }

        HttpEntity<StockRequestDto> entity = new HttpEntity<>(request, headers);

        restTemplate.postForEntity(ERP_BASE_URL + "/restore", entity, Void.class);
    }



    /** 재고 수정 */
    public void updateStock(StockRequestDto request) {
        restTemplate.put(ERP_BASE_URL + "/update", request);
    }

    /** 공통: ERP API에 전달할 HttpEntity 생성 */
    private HttpEntity<StockRequestDto> buildEntity(StockRequestDto request, HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 프론트에서 넘어온 Authorization 헤더 가져와서 그대로 ERP에 전달
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }

        return new HttpEntity<>(request, headers);
    }
}
