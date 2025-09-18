package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final RestTemplate restTemplate;

    public Object getErpEmployees() { // 반환 타입을 String -> Object로 변경
        final String erpApiUrl = "http://localhost:8081/api/employees";
        try {
            // 응답을 String이 아닌 Object.class로 받습니다.
            // Spring이 자동으로 JSON을 Java 객체(List<Map<...>>)로 변환해 줍니다.
            Object response = restTemplate.getForObject(erpApiUrl, Object.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 발생 시 null이나 빈 리스트를 반환할 수 있습니다.
            return null;
        }
    }
}
