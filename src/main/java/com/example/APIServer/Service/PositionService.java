package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final RestTemplate restTemplate;

    public Object getErpPositions() {
        final String erpApiUrl = "http://localhost:8081/api/positions";
        try {
            return restTemplate.getForObject(erpApiUrl, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}