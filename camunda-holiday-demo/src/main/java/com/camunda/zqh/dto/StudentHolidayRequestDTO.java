package com.camunda.zqh.dto;

import lombok.Data;

@Data
public class StudentHolidayRequestDTO {
    private int studentId;
    private String studentName;
    private int requestDaysCount;
}
