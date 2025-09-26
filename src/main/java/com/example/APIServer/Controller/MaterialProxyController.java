package com.example.APIServer.Controller;

import com.example.APIServer.Service.MaterialProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proxy/materials")  // 프론트에서는 이 주소만 호출
@RequiredArgsConstructor
public class MaterialProxyController {

    private final MaterialProxyService materialProxyService;

    // ERP 자재 전체 조회 + 조건 검색
    @GetMapping
    public Object getMaterials(
            @RequestParam(required = false) String materialNm,
            @RequestParam(required = false) String category
    ) {
        return materialProxyService.getMaterials(materialNm, category);
    }

    // ERP 자재 단건 조회
    @GetMapping("/{materialId}")
    public Object getMaterial(@PathVariable Integer materialId) {
        return materialProxyService.getMaterial(materialId);
    }

    // ERP 자재 저장
    @PostMapping
    public Object saveMaterial(@RequestBody Object materialDto) {
        return materialProxyService.saveMaterial(materialDto);
    }

    // ERP 자재 삭제
    @DeleteMapping("/{materialId}")
    public void deleteMaterial(@PathVariable Integer materialId) {
        materialProxyService.deleteMaterial(materialId);
    }
}
