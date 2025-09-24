package com.example.APIServer.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션 전반에서 사용될 공용 Bean들을 정의하는 설정 클래스입니다.
 * @Configuration: Spring에게 이 클래스가 프로젝트의 설정을 담고 있는
 * 특별한 클래스임을 알려주는 어노테이션입니다.
 */
@Configuration
public class AppConfig {

    /**
     * 다른 서버와 HTTP 통신을 하기 위한 RestTemplate 객체를 Bean으로 등록합니다.
     * @Bean: 이 어노테이션이 붙은 메소드가 반환하는 객체(여기서는 new RestTemplate())를
     * Spring이 직접 관리하는 부품(Bean)으로 등록하라는 의미입니다.
     * 이렇게 등록된 Bean은 다른 컴포넌트(Service, Controller 등)에서
     * 생성자 주입 등을 통해 편리하게 가져다 쓸 수 있습니다.
     * @return Spring 컨테이너가 관리할 RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        // 새로운 RestTemplate 인스턴스를 생성하여 반환합니다.
        return new RestTemplate();
    }
}