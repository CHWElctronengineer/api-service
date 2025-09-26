package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MaterialProxyService {

    private final RestTemplate restTemplate;

    private static final String ERP_BASE_URL = "http://localhost:8081/api/materials";

    // ERP 자재 전체 조회
    public Object getMaterials(String materialNm, String category) {
        String url = ERP_BASE_URL;
        boolean hasQuery = false;

        if (materialNm != null) {
            url += (hasQuery ? "&" : "?") + "materialNm=" + materialNm;
            hasQuery = true;
        }
        if (category != null) {
            url += (hasQuery ? "&" : "?") + "category=" + category;
        }

        return restTemplate.getForObject(url, Object.class);
    }

    // ERP 자재 단건 조회
    public Object getMaterial(Integer materialId) {
        String url = ERP_BASE_URL + "/" + materialId;
        return restTemplate.getForObject(url, Object.class);
    }

    // ERP 자재 저장
    public Object saveMaterial(Object materialDto) {
        return restTemplate.postForObject(ERP_BASE_URL, materialDto, Object.class);
    }

    // ERP 자재 삭제
    public void deleteMaterial(Integer materialId) {
        restTemplate.delete(ERP_BASE_URL + "/" + materialId);
    }
}

