package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 ERP 서버의 직원(Employee) API와 통신하는 로직을 담당하는 서비스 클래스입니다.
 * RestTemplate을 사용하여 ERP 서버에 HTTP 요청을 보내고 응답을 받아 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    // AppConfig에 Bean으로 등록된 RestTemplate 객체를 생성자 주입 방식으로 주입받습니다.
    private final RestTemplate restTemplate;

    private final String ERP_API_BASE_URL = "http://localhost:8081/api/employees";

    /**
     * ERP 서버로부터 조건에 맞는 직원 목록을 가져오는 기능 (GET)
     * @param employeeNm 검색할 직원 이름 (선택 사항)
     * @return ERP 서버로부터 받은 데이터
     */
    public Object getErpEmployees(String employeeNm) {
        String erpApiUrl = ERP_API_BASE_URL;
        // employeeName 파라미터가 있으면 URL에 쿼리 스트링을 추가
        if (employeeNm != null && !employeeNm.isEmpty()) {
            erpApiUrl += "?employeeNm=" + employeeNm;
        }
        return restTemplate.getForObject(erpApiUrl, Object.class);
    }

    /**
     * ERP 서버에 새로운 직원을 생성하는 기능 (POST)
     * @param payload 생성할 직원 데이터
     * @return ERP 서버로부터 받은 생성된 데이터
     */
    public Object createEmployee(Object payload) {
        return restTemplate.postForObject(ERP_API_BASE_URL, payload, Object.class);
    }

    /**
     * ERP 서버의 특정 직원을 수정하는 기능 (PUT)
     * @param employeeId 수정할 직원의 ID
     * @param payload 수정할 직원 데이터
     */
    public void updateEmployee(String employeeId, Object payload) {
        final String url = ERP_API_BASE_URL + "/" + employeeId;
        restTemplate.put(url, payload);
    }

    /**
     * ERP 서버의 특정 직원을 삭제하는 기능 (DELETE)
     * @param employeeId 삭제할 직원의 ID
     */
    public void deleteEmployee(String employeeId) {
        final String url = ERP_API_BASE_URL + "/" + employeeId;
        restTemplate.delete(url);
    }


    /**
     * ERP 서버로부터 모든 직원 목록을 조회합니다.
     *
     * @return 성공 시 ERP 서버로부터 받은 직원 정보 목록 (JSON 배열이 Object 타입으로 변환됨).
     * 실패 시 콘솔에 에러를 출력하고 null을 반환합니다.
     */
//    public Object getErpEmployees() {
//        // 호출할 외부 ERP API의 엔드포인트 주소입니다.
//        final String erpApiUrl = "http://localhost:8081/api/employees";
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