package com.example.APIServer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 외부 서버인 '드론 서버'의 API와 통신하는 로직을 담당하는 서비스 클래스입니다.
 * RestTemplate을 사용하여 HTTP 요청을 보내고 응답을 받아 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class DronImageService {

    // Spring Bean으로 등록된 RestTemplate 객체를 주입받습니다.
    private final RestTemplate restTemplate;

    /**
     * 드론 서버로부터 모든 이미지의 메타데이터 목록을 조회합니다.
     * @return 드론 서버로부터 받은 응답 데이터. Spring이 JSON 배열을 Java 객체(List<Map<...>>)로 변환합니다.
     */
    public Object getDroneImages() {
        // 호출할 드론 서버의 API 엔드포인트 주소
        final String droneApiUrl = "http://localhost:8084/api/images";
        try {
            // HTTP GET 요청을 보내고, 응답 본문을 Object 타입으로 받습니다.
            Object response = restTemplate.getForObject(droneApiUrl, Object.class);
            return response;
        } catch (Exception e) {
            System.err.println("드론 서버 API 호출 중 오류 발생: " + e.getMessage());
            return null; // 오류 발생 시 null을 반환합니다.
        }
    }

    /**
     * ID를 이용해 드론 서버로부터 특정 이미지의 실제 파일 데이터를 조회합니다.
     * @param id 조회할 이미지의 고유 ID
     * @return 이미지의 바이너리 데이터(byte[])와 헤더 정보를 포함하는 ResponseEntity
     */
    public ResponseEntity<byte[]> getDroneImageById(Long id) {
        final String droneApiUrl = "http://localhost:8084/api/images/" + id;
        try {
            // HTTP GET 요청을 보내고, 응답 전체(상태 코드, 헤더, 본문)를 ResponseEntity 객체로 받습니다.
            ResponseEntity<byte[]> responseFromDroneServer = restTemplate.getForEntity(droneApiUrl, byte[].class);

            // 디버깅을 위해 수신한 응답의 Content-Type을 콘솔에 출력합니다.
            System.out.println(">>> [API 서버] 드론 서버로부터 받은 응답 Content-Type: " + responseFromDroneServer.getHeaders().getContentType());

            // 수신한 응답을 그대로 컨트롤러에 반환합니다.
            return responseFromDroneServer;
        } catch (Exception e) {
            System.err.println("개별 이미지 조회 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 500 Internal Server Error 상태를 반환합니다.
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 클라이언트로부터 받은 이미지 파일(MultipartFile)을 드론 서버로 전달(업로드)합니다.
     * @param file 업로드할 이미지 파일
     * @return 드론 서버로부터 받은 응답 ResponseEntity
     */
    public ResponseEntity<String> uploadImage(MultipartFile file) {
        final String droneApiUrl = "http://localhost:8084/api/images/upload";

        try {
            // 1. HTTP 헤더를 설정합니다. 파일 업로드에는 'multipart/form-data' 타입을 사용합니다.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. HTTP 요청 본문(Body)을 생성합니다.
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 2-1. MultipartFile의 내용을 ByteArrayResource로 감싸줍니다.
            //      RestTemplate이 파일 데이터를 처리할 수 있는 형태입니다.
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                // 원본 파일명을 함께 전송하기 위해 getFilename() 메소드를 오버라이드합니다.
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            // "file"이라는 키 값으로 파일 리소스를 본문에 추가합니다.
            body.add("file", resource);

            // 3. 헤더와 본문을 HttpEntity 객체로 합쳐서 하나의 요청으로 만듭니다.
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. RestTemplate을 사용하여 드론 서버에 POST 요청을 전송하고, 응답을 String 형태로 받습니다.
            return restTemplate.postForEntity(droneApiUrl, requestEntity, String.class);

        } catch (Exception e) {
            System.err.println("이미지 업로드 중계 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to proxy image upload");
        }
    }
}