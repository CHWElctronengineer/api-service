package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DronImageService {

    private final RestTemplate restTemplate;

    public Object getDroneImages() {
        final String droneApiUrl = "http://localhost:8084/api/images";
        try {
            // 드론 서버의 응답(JSON 배열)을 자바 객체(List<Map<...>>)로 자동 변환하여 받습니다.
            Object response = restTemplate.getForObject(droneApiUrl, Object.class);
            return response;
        } catch (Exception e) {
            System.err.println("드론 서버 API 호출 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 null 반환
            return null;
        }
    }
    public ResponseEntity<byte[]> getDroneImageById(Long id) {
        final String droneApiUrl = "http://localhost:8084/api/images/" + id;
        try {
            // 1. ✅ 먼저 restTemplate으로 요청을 보내고, 그 결과를 변수에 저장합니다.
            ResponseEntity<byte[]> responseFromDroneServer = restTemplate.getForEntity(droneApiUrl, byte[].class);

            // 2. ✅ 그 다음에 저장된 변수를 사용해서 로그를 출력합니다.
            System.out.println(">>> [API 서버] 드론 서버로부터 받은 응답 Content-Type: " + responseFromDroneServer.getHeaders().getContentType());

            // 3. ✅ 마지막으로 변수에 담긴 결과를 반환합니다.
            return responseFromDroneServer;
        } catch (Exception e) {
            System.err.println("개별 이미지 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}