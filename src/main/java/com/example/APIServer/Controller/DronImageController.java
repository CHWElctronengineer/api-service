package com.example.APIServer.Controller;

import com.example.APIServer.Service.DronImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
public class DronImageController {


    private final DronImageService dronImageService;

    @GetMapping("/drone-images") // 드론 이미지를 가져올 새로운 경로
    public Object getDroneImagesFromProxy() {
        // DronImageService의 메소드를 호출하여 결과를 반환합니다.
        return dronImageService.getDroneImages();
    }
    @GetMapping("/drone-images/{id}")
    public ResponseEntity<byte[]> getDroneImageById(@PathVariable Long id) {
        // 서비스에 요청을 전달하고 받은 응답을 그대로 클라이언트에 반환합니다.
        return dronImageService.getDroneImageById(id);
    }

}
