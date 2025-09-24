package com.example.APIServer.Controller;

import com.example.APIServer.Service.ProjectPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ERP 서버의 생산 계획(Project Plan) 관련 API를 중계(Proxy)하는 컨트롤러입니다.
 * 클라이언트의 요청을 받아 ProjectPlanService를 통해 ERP 서버로 전달하고,
 * 그 결과를 다시 클라이언트에게 반환하는 역할을 담당합니다.
 * 모든 경로는 "/api/proxy" 아래에 위치합니다.
 */
@RestController
@RequestMapping("/api/proxy") // "/api/proxy"로 시작하는 모든 요청을 이 컨트롤러가 처리합니다.
@RequiredArgsConstructor
public class ProjectPlanController {

    /**
     * 실제 ERP 서버와의 통신 로직을 담당하는 서비스입니다.
     * final 키워드와 @RequiredArgsConstructor를 통해 의존성이 주입됩니다.
     */
    private final ProjectPlanService erpApiService;

    /**
     * ERP 서버로부터 모든 생산 계획 목록을 조회하는 요청을 중계합니다.
     * 클라이언트는 이 API('/api/proxy/project_plans')를 호출하여 생산 계획 데이터를 가져올 수 있습니다.
     *
     * @return ERP 서버로부터 받은 생산 계획 정보 목록 (JSON 배열 형태로 클라이언트에게 반환됩니다)
     */
    @GetMapping("/project_plans")
    public Object getErpDataFromProxy() {
        // ProjectPlanService에 구현된 메소드를 호출하여 결과를 그대로 반환합니다.
        return erpApiService.getErpProjectPlans();
    }
}