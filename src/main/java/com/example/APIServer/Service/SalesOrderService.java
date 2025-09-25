package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SalesOrderService {
    private final RestTemplate restTemplate;

    // ERP의 SalesOrder 목록을 가져오는 기능 (GET)
    public Object getErpSalesOrders(String customerId, String vesselId) {
        // 실제 ERP 서버 주소
        final String erpApiUrl = "http://localhost:8081/api/sales_orders?customerId=" + (customerId != null ? customerId : "") + "&vesselId=" + (vesselId != null ? vesselId : "");
        return restTemplate.getForObject(erpApiUrl, Object.class);
    }

    // ERP에 SalesOrder를 생성(POST)하는 기능
    public Object createSalesOrder(Object payload) {
        final String erpApiUrl = "http://localhost:8081/api/sales_orders";
        return restTemplate.postForObject(erpApiUrl, payload, Object.class);
    }

    // ERP의 SalesOrder를 수정(PUT)하는 기능
    public void updateSalesOrder(String salesOrderId, Object payload) {
        final String erpApiUrl = "http://localhost:8081/api/sales_orders/" + salesOrderId;
        restTemplate.put(erpApiUrl, payload);
    }

    //  ERP의 SalesOrder를 삭제(DELETE)하는 기능
    public void deleteSalesOrder(String salesOrderId) {
        final String erpApiUrl = "http://localhost:8081/api/sales_orders/" + salesOrderId;
        restTemplate.delete(erpApiUrl);
    }
}
