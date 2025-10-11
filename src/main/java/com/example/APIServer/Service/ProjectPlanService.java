package com.example.APIServer.Service;

import com.example.APIServer.Dto.PlanIdDto;
import com.example.APIServer.Dto.ProjectPlanDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 외부 ERP 서버의 생산 계획(Project Plan) API와 통신하는 로직을 담당하는 서비스 클래스입니다.
 * RestTemplate을 사용하여 ERP 서버에 HTTP 요청을 보내고 응답을 받아 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectPlanService {

    // AppConfig에 Bean으로 등록된 RestTemplate 객체를 생성자 주입 방식으로 주입받습니다.
    private final RestTemplate restTemplate;
    private final String ERP_API_URL = "http://localhost:8081/api/project_plans";
    private final String MES_REF_API_URL = "http://localhost:8082/api/erp-plan-refs";

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

    /**
     * ERP 서버로 생산 계획 수정(PUT) 요청을 중계합니다.
     * @param planId 수정할 계획의 ID
     * @param dto 수정할 내용이 담긴 데이터
     */
    public void updateErpProjectPlan(String planId, ProjectPlanDto dto) {
        try {
            // HTTP PUT 요청을 보내고, 별도의 응답은 받지 않습니다.
            restTemplate.put(ERP_API_URL + "/" + planId, dto);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외를 다시 던져서 Controller 단에서 오류를 인지하게 할 수 있습니다.
            throw new RuntimeException("ERP 서버로 수정 요청 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * ERP의 모든 생산 계획 ID를 MES로 동기화합니다.
     */
    public void syncAllPlanIdsToMes() {
        try {
            // 1. ERP 서버(8081)에서 모든 생산 계획 '목록'을 조회
            ProjectPlanDto[] erpPlans = restTemplate.getForObject(ERP_API_URL, ProjectPlanDto[].class);

            if (erpPlans == null || erpPlans.length == 0) {
                System.out.println("ERP에서 동기화할 생산 계획이 없습니다.");
                return;
            }

            // 2. 전체 계획 목록에서 'planId'만 추출하여 List<String>으로 변환
            List<String> planIds = Arrays.stream(erpPlans)
                    .map(ProjectPlanDto::getPlanId)
                    .collect(Collectors.toList());

            // 3. 추출한 ID들을 하나씩 MES 서버(8082)로 전송
            for (String id : planIds) {
                PlanIdDto dto = new PlanIdDto();
                dto.setPlanId(id);
                // MES의 새 엔드포인트로 POST 요청을 보냄
                restTemplate.postForEntity(MES_REF_API_URL, dto, String.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MES로 Plan ID 동기화 중 오류 발생", e);
        }
    }


    // ==================================================

    // 진행률만 업데이트 중계
    public void updateErpProjectProgress(String planId, BigDecimal progressRate) {
        String targetUrl = ERP_API_URL + "/" + planId + "/progress";

        log.info("--- 🚀 ERP 서버(8081)로 PUT 요청 발송 시작 ---");
        log.info("  - Target URL: {}", targetUrl);
        log.info("  - HTTP Method: PUT");
        log.info("  - Request Body (progressRate): {}", progressRate);

        try {
            // 1. HTTP 헤더를 만들고, Content-Type을 JSON으로 명확하게 지정합니다.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. 헤더와 데이터를 담은 요청 객체(HttpEntity)를 생성합니다.
            HttpEntity<BigDecimal> requestEntity = new HttpEntity<>(progressRate, headers);

            // 3. restTemplate.put 대신 exchange 메서드를 사용하여 명시적으로 요청을 보냅니다.
            restTemplate.exchange(targetUrl, HttpMethod.PUT, requestEntity, Void.class);

            log.info("--- ✅ ERP 서버(8081)로 요청 발송 성공 ---");

        } catch (Exception e) {
            // 4. 에러 발생 시, 로그를 더 상세하게 남깁니다.
            log.error("--- ❌ ERP 서버(8081)로 요청 발송 중 오류 발생! ---", e);
            throw new RuntimeException("ERP 서버로 진행률 업데이트 요청 중 오류가 발생했습니다.", e);
        }
    }
}