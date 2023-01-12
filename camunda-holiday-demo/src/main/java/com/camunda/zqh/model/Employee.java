package com.camunda.zqh.model;

import lombok.Data;

@Data
public class Employee {

    private Long employeeId;

    private String employeeName;

    public Employee(Long employeeId) {
        this.employeeId = employeeId;
    }
}
