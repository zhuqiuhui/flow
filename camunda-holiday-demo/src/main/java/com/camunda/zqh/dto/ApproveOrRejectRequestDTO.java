package com.camunda.zqh.dto;

import lombok.Data;

@Data
public class ApproveOrRejectRequestDTO {
    private Long holidayRequestId;
    private Integer employeeId;
    private Integer holidayStatusId;
    private String holidayStage;
    private String businessKey;
}
