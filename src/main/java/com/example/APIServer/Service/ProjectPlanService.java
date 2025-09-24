package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 ERP 서버의 생산 계획(Project Plan) API와 통신하는 로직을 담당하는 서비스 클래스입니다.
 * RestTemplate을 사용하여 ERP 서버에 HTTP 요청을 보내고 응답을 받아 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class ProjectPlanService {

    // AppConfig에 Bean으로 등록된 RestTemplate 객체를 생성자 주입 방식으로 주입받습니다.
    private final RestTemplate restTemplate;

    /**
     * ERP 서버로부터 모든 생산 계획 목록을 조회합니다.
     *
     * @return 성공 시 ERP 서버로부터 받은 생산 계획 정보 목록 (JSON 배열이 Object 타입으로 변환됨).
     * 실패 시 콘솔에 에러를 출력하고 null을 반환합니다.
     */
    public Object getErpProjectPlans() {
        // 호출할 외부 ERP API의 엔드포인트 주소입니다.
        final String erpApiUrl = "http://localhost:8081/api/project_plans";
        try {
            // HTTP GET 요청을 보내고, 응답 본문을 Object 타입으로 받습니다.
            // Spring의 HttpMessageConverter가 JSON 응답을 자동으로 Java 객체(이 경우 List<Map<String, Object>>)로 변환해 줍니다.
            Object response = restTemplate.getForObject(erpApiUrl, Object.class);
            return response;
        } catch (Exception e) {
            // RestTemplate 호출 중 발생할 수 있는 네트워크 오류나 서버 오류 등을 처리합니다.
            // 예외 발생 시, 콘솔에 에러 로그를 출력합니다.
            e.printStackTrace();
            // 오류가 발생했음을 호출한 쪽에 알리기 위해 null을 반환합니다.
            return null;
        }
    }
}