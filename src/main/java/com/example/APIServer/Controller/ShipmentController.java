package com.example.APIServer.Controller;

import com.example.APIServer.Service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService ApiShipmentService;

    @GetMapping("/shipments/by-order/{salesOrderId}")
    public Object getMesDataFromProxy(@PathVariable String salesOrderId){
        return ApiShipmentService.getMESshipmentBySalesOrderId(salesOrderId);
    }
}
