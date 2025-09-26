package com.example.APIServer.Dto;


import lombok.Data;

/**
 * MES -> API 서버 -> ERP 서버로 전달될 재고 조정 요청 DTO
 */
@Data
public class StockRequestDto {
    private String inventoryId;   // 선택: 특정 재고 ID
    private Integer materialId;   // 자재 ID (필수)
    private String warehouse;     // 창고
    private String location;      // 위치
    private int quantity;         // 수량 (양수=증가, 음수=차감)
}
