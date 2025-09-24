package com.example.APIServer.Controller;

import com.example.APIServer.Service.DronImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 드론 서버의 이미지 관련 API를 중계(Proxy)하는 컨트롤러입니다.
 * 클라이언트의 요청을 받아 DroneImageService를 통해 실제 드론 서버로 전달하고,
 * 그 응답을 다시 클라이언트에게 반환하는 역할을 합니다.
 * 모든 경로는 "/api/proxy" 아래에 위치합니다.
 */
@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
public class DroneImageController {

    /**
     * 실제 드론 서버와의 통신 로직을 담당하는 서비스입니다.
     * @RequiredArgsConstructor에 의해 생성자에서 자동으로 의존성이 주입됩니다.
     */
    private final DronImageService dronImageService;

    /**
     * 드론 서버에 등록된 모든 이미지의 메타데이터 목록을 조회하는 요청을 중계합니다.
     *
     * @return 드론 서버로부터 받은 이미지 정보 목록 (JSON 배열 형태로 반환됨)
     */
    // 특정 리소스의 목록
    @GetMapping("/drone-images")
    public Object getDroneImagesFromProxy() {
        // DronImageService에 구현된 메소드를 호출하여 결과를 그대로 반환합니다.
        return dronImageService.getDroneImages();
    }

    /**
     * 특정 ID를 가진 이미지 파일의 실제 데이터(byte[])를 조회하는 요청을 중계합니다.
     *
     * @param id 조회할 이미지의 고유 ID
     * @return 이미지의 바이너리 데이터와 Content-Type 헤더를 포함하는 ResponseEntity
     */
    // 목록 안의 개별 항목
    @GetMapping("/drone-images/{id}")
    public ResponseEntity<byte[]> getDroneImageById(@PathVariable Long id) {
        // 서비스에 요청을 전달하고, 받은 응답(ResponseEntity)을 그대로 클라이언트에 반환합니다.
        // 이 응답에는 이미지 데이터뿐만 아니라 Content-Type 같은 중요한 헤더 정보도 포함됩니다.
        return dronImageService.getDroneImageById(id);
    }
}