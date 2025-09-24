package com.example.APIServer.Repository;

import com.example.APIServer.Entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 'api_logs' 테이블에 접근하기 위한 Spring Data JPA 리포지토리(Repository) 인터페이스입니다.
 *
 * @Repository: 이 인터페이스가 데이터 접근 계층의 컴포넌트(Bean)임을 Spring에게 알려줍니다.
 * 또한, JPA 관련 예외를 Spring의 DataAccessException으로 변환해주는 역할도 합니다.
 *
 * JpaRepository<ApiLogEntity, Long>:
 * - JpaRepository를 상속받아, 기본적인 CRUD(Create, Read, Update, Delete) 메소드를 자동으로 제공받습니다.
 * (예: save(), findById(), findAll(), deleteById() 등)
 * - <ApiLogEntity, Long>: 이 리포지토리가 'ApiLogEntity'라는 엔티티를 다루고,
 * 해당 엔티티의 기본 키(Primary Key) 타입이 'Long'임을 명시합니다.
 *
 * JpaSpecificationExecutor<ApiLogEntity>:
 * - 동적인 쿼리를 작성하고 실행할 수 있는 기능을 제공합니다.
 * - 예를 들어, 검색 조건이 다양하게 변하는 복잡한 조회 기능을 구현할 때 유용합니다.
 */
@Repository
public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long>, JpaSpecificationExecutor<ApiLogEntity> {

    // 이 인터페이스의 본문은 비워두어도 됩니다.
    // Spring Data JPA가 상속받은 메소드들의 구현을 런타임에 자동으로 생성하여 제공하기 때문입니다.
    // 필요한 경우, 여기에 직접 쿼리 메소드를 정의할 수도 있습니다.
    // (예: List<ApiLogEntity> findByHttpMethod(String method);)
}