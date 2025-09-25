package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final RestTemplate restTemplate;

    public Object getMESshipmentBySalesOrderId(String salesOrderId) {
        // ✅ [수정] MES API에 쿼리 파라미터를 추가하여 특정 주문의 출하 정보만 요청
        final String mesApiUrl = "http://localhost:8082/api/shipments?salesOrderId=" + salesOrderId;
        try {
            return restTemplate.getForObject(mesApiUrl, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            // 출하 정보가 없는 경우는 정상일 수 있으므로 null 반환
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//    public Object getMESshipments() {
//        // 호출할 외부 MES API의 엔드포인트 주소입니다.
//        final String erpApiUrl = "http://localhost:8082/api/shipments";
//        try {
//            // HTTP GET 요청을 보내고, 응답 본문을 Object 타입으로 받습니다.
//            // Spring의 HttpMessageConverter가 JSON 응답을 자동으로 Java 객체(이 경우 List<Map<String, Object>>)로 변환해 줍니다.
//            Object response = restTemplate.getForObject(erpApiUrl, Object.class);
//            return response;
//        } catch (Exception e) {
//            // RestTemplate 호출 중 발생할 수 있는 네트워크 오류나 서버 오류 등을 처리합니다.
//            // 예외 발생 시, 콘솔에 에러 로그를 출력합니다.
//            e.printStackTrace();
//            // 오류가 발생했음을 호출한 쪽에 알리기 위해 null을 반환합니다.
//            return null;
//        }
//    }

}
