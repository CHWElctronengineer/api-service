package com.example.APIServer.Repository;

import com.example.APIServer.Entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long>, JpaSpecificationExecutor<ApiLogEntity> {

    // httpMethod를 기준으로 검색하고, Sort 객체로 정렬도 할 수 있는 메소드
    List<ApiLogEntity> findByHttpMethod(String method, Sort sort);
}