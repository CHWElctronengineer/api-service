package com.example.APIServer.Controller;

import com.example.APIServer.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ERP 서버의 직원(Employee) 관련 API를 중계(Proxy)하는 컨트롤러입니다.
 * 클라이언트의 요청을 받아 EmployeeService를 통해 ERP 서버로 전달하고,
 * 그 결과를 다시 클라이언트에게 반환하는 역할을 담당합니다.
 * 모든 경로는 "/api/proxy" 아래에 위치합니다.
 */
@RestController
@RequestMapping("/api/proxy/employees") // "/api/proxy"로 시작하는 모든 요청을 이 컨트롤러가 처리합니다.
@RequiredArgsConstructor
public class EmployeeController {

    /**
     * 실제 ERP 서버와의 통신 로직을 담당하는 서비스입니다.
     * final 키워드와 @RequiredArgsConstructor를 통해 의존성이 주입됩니다.
     */
    private final EmployeeService employeeService;

    /**
     * 직원 목록을 조회하는 API입니다. 이름으로 검색하는 기능을 포함합니다.
     * @param employeeNm (선택 사항) 이름으로 검색하기 위한 쿼리 파라미터
     * @return 직원 정보 목록
     */
    @GetMapping
    public Object getEmployees(@RequestParam(required = false) String employeeNm) {
        return employeeService.getErpEmployees(employeeNm);
    }

    /**
     * 새로운 직원을 생성하는 API입니다.
     * @param payload 생성할 직원 정보가 담긴 JSON 데이터 (Request Body)
     * @return 생성된 직원 정보
     */
    @PostMapping
    public Object createEmployee(@RequestBody Object payload) {
        return employeeService.createEmployee(payload);
    }

    /**
     * 특정 직원의 정보를 수정하는 API입니다.
     * @param employeeId 수정할 직원의 ID (URL 경로 변수)
     * @param payload 수정할 정보가 담긴 JSON 데이터 (Request Body)
     */
    @PutMapping("/{employeeId}")
    public void updateEmployee(
            @PathVariable String employeeId,
            @RequestBody Object payload) {
        employeeService.updateEmployee(employeeId, payload);
    }

    /**
     * 특정 직원을 삭제하는 API입니다.
     * @param employeeId 삭제할 직원의 ID (URL 경로 변수)
     */
    @DeleteMapping("/{employeeId}")
    public void deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
    }
}