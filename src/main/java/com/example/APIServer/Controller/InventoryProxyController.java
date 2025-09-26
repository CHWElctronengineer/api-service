package com.example.APIServer.Controller;

import com.example.APIServer.Dto.StockRequestDto;
import com.example.APIServer.Service.InventoryProxyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proxy/inventory")
@RequiredArgsConstructor
public class InventoryProxyController {

    private final InventoryProxyService inventoryProxyService;

    /** ERP 전체 재고 조회 */
    @GetMapping
    public ResponseEntity<Object> getAllInventories() {
        return ResponseEntity.ok(inventoryProxyService.getAllInventories());
    }

    /** 재고 차감 */
    @PostMapping("/deduct")
    public ResponseEntity<Void> deductStock(@RequestBody StockRequestDto request) {
        inventoryProxyService.deductStock(request);
        return ResponseEntity.ok().build();
    }

    /** 재고 복구 */
    @PostMapping("/restore")
    public ResponseEntity<Void> restoreStock(
            @RequestBody StockRequestDto request,
            HttpServletRequest httpRequest // 프론트 요청 헤더 가져오기
    ) {
        inventoryProxyService.restoreStock(request, httpRequest);
        return ResponseEntity.ok().build();
    }


    /** 재고 수정 */
    @PutMapping("/update")
    public ResponseEntity<Void> updateStock(@RequestBody StockRequestDto request) {
        inventoryProxyService.updateStock(request);
        return ResponseEntity.ok().build();
    }
}

