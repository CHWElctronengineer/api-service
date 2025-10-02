package com.example.APIServer.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

// MES React 앱으로부터 수정할 데이터를 받기 위한 데이터 상자
@Data
public class ProjectPlanDto {
    // React에서 보낼 수 있는 모든 필드를 정의합니다.
    private String planId;
    private String projectId;
    private String vesselId;
    private String planScope;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal progressRate;
    private Integer status;
    private String remark;
    // isNew, _tempId 같은 프론트엔드 전용 필드는 포함하지 않습니다.
}
