package com.example.APIServer.Controller;

import com.example.APIServer.Service.ErpApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proxy") // 기존 API와 겹치지 않도록 '/proxy' 같은 경로 사용
@RequiredArgsConstructor
public class ErpProxyController {

    private final ErpApiService erpApiService;

    @GetMapping("/boms")
    public Object getErpDataFromProxy() { // 반환 타입을 String -> Object로 변경
        return erpApiService.getErpBoms();
    }
}
