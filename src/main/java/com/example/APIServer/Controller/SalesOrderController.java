package com.example.APIServer.Controller;


import com.example.APIServer.Service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proxy/sales-orders") // 경로를 sales-orders로 명확히 함
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @GetMapping
    public Object getSalesOrders(@RequestParam(required = false) String customerId, @RequestParam(required = false) String vesselId) {
        return salesOrderService.getErpSalesOrders(customerId, vesselId);
    }

    // POST 요청을 중계하는 엔드포인트
    @PostMapping
    public Object createSalesOrder(@RequestBody Object payload) {
        return salesOrderService.createSalesOrder(payload);
    }

    //  PUT 요청을 중계하는 엔드포인트
    @PutMapping("/{salesOrderId}")
    public void updateSalesOrder(@PathVariable String salesOrderId, @RequestBody Object payload) {
        salesOrderService.updateSalesOrder(salesOrderId, payload);
    }

    // DELETE 요청을 중계하는 엔드포인트
    @DeleteMapping("/{salesOrderId}")
    public void deleteSalesOrder(@PathVariable String salesOrderId) {
        salesOrderService.deleteSalesOrder(salesOrderId);
    }
}
