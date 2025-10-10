package com.example.APIServer.Controller;

import com.example.APIServer.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proxy/dashboard")
@RequiredArgsConstructor
public class DashboardMesController {

    private final DashboardService dashboardService;

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getErpProjects() {
        return ResponseEntity.ok(dashboardService.fetchProjectsFromErp());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Map<String, Object>>> getErpCustomers() {
        return ResponseEntity.ok(dashboardService.fetchCustomersFromErp());
    }

    @GetMapping("/materials")
    public ResponseEntity<List<Map<String, Object>>> getErpMaterials() {
        return ResponseEntity.ok(dashboardService.fetchMaterialsFromErp());
    }

    @GetMapping("/purchaseOrders")
    public ResponseEntity<List<Map<String, Object>>> getErpPurchaseOrders() {
        return ResponseEntity.ok(dashboardService.fetchPurchaseOrdersFromErp());
    }

    //생산계획
    @GetMapping("/project_plans")
    public ResponseEntity<List<Map<String, Object>>> getErpProjectPlans() {
        return ResponseEntity.ok(dashboardService.fetchProjectPlansFromErp());
    }

    // 6. 출하 현황 (Sales Orders)
    @GetMapping("/sales_orders")
    public ResponseEntity<List<Map<String, Object>>> getErpSalesOrders() {
        return ResponseEntity.ok(dashboardService.fetchSalesOrdersFromErp());
    }

    //7. 인사/사원 현황 (Employees)
    @GetMapping("/employees")
    public ResponseEntity<List<Map<String, Object>>> getErpEmployees() {
        return ResponseEntity.ok(dashboardService.fetchEmployeesFromErp());
    }

}
