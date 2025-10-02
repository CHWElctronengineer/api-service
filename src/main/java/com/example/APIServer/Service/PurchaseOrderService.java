package com.example.APIServer.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final RestTemplate restTemplate;
    private final String ERP_API_URL = "http://localhost:8081/api/purchaseOrders";

    public Object getErpPurchaseOrders() {
        try {
            // ERP 서버의 응답을 Object 타입으로 받아서 그대로 반환합니다.
            return restTemplate.getForObject(ERP_API_URL, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
