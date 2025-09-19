package com.example.APIServer.Repository;

import com.example.APIServer.Entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long>, JpaSpecificationExecutor<ApiLogEntity> {
    // 비워두시면 됩니다.
}
