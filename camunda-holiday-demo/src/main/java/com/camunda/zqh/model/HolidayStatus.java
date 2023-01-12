package com.camunda.zqh.model;

import lombok.Data;

@Data
public class HolidayStatus {

    private Integer holidayStatusId;

    private String status;

    public HolidayStatus(Integer holidayStatusId) {
        this.holidayStatusId = holidayStatusId;
    }
}
