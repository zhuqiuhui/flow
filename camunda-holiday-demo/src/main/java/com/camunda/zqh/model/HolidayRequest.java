package com.camunda.zqh.model;

import lombok.Data;

@Data
public class HolidayRequest {

    private Long requestId;

    private String businessKey;

    private Student student;

    /**
     * holiday status
     */
    private HolidayStatus holidayStatus;

    private String holidayStage;

    private Employee employeeId;

    private int requestDays;

}
