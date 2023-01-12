package com.camunda.zqh.dto;

import lombok.Data;

@Data
public class ProcessTaskResponseDTO {

    private String processDefinitionId;

    private String processInstanceId;

    private String created;

    private String name;

    private String id;
}
