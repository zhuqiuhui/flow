package com.camunda.zqh.service;

import com.camunda.zqh.dto.ApproveOrRejectRequestDTO;
import com.camunda.zqh.dto.ProcessTaskResponseDTO;
import com.camunda.zqh.dto.StudentHolidayRequestDTO;
import com.camunda.zqh.model.Employee;
import com.camunda.zqh.model.HolidayRequest;
import com.camunda.zqh.model.HolidayStatus;
import com.camunda.zqh.model.Student;
import com.camunda.zqh.repository.HolidayRequestRepository;
import org.camunda.bpm.engine.ProcessEngine;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
public class CustomService {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HolidayRequestRepository holidayRequestRepository;
	
    @Value("${bpm.holidayProcessId}")
    private String holidayRequestBpmKey;

    @Value("${bpm.activeTask}")
    private String activeTaskUrl;

    @Value("${bpm.completeTask}")
    private String completeTaskUrl;


    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }

    public void startProcess(String businessKey, StudentHolidayRequestDTO studentHolidayRequestDTO) {
        // 1. 设置变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentId", studentHolidayRequestDTO.getStudentId());
        variables.put("studentName", studentHolidayRequestDTO.getStudentName());
        variables.put("holidayDaysCount", studentHolidayRequestDTO.getRequestDaysCount());

        // 2.开启流程实例
        processEngine.getRuntimeService().startProcessInstanceByKey(holidayRequestBpmKey, businessKey, variables);

        // 3.
        startHolidayRequestProcess(studentHolidayRequestDTO, businessKey);
    }


    public void startHolidayRequestProcess(StudentHolidayRequestDTO studentHolidayRequestDTO, String businessKey) {
        // 1. save holiday request
        HolidayRequest holidayRequest = new HolidayRequest();
        holidayRequest.setBusinessKey(businessKey);
        holidayRequest.setHolidayStatus(new HolidayStatus(1));
        holidayRequest.setEmployeeId(new Employee(1L));
        holidayRequest.setHolidayStage("class teacher approval pending");
        holidayRequest.setStudent(new Student(studentHolidayRequestDTO.getStudentId()));
        holidayRequest.setRequestDays(studentHolidayRequestDTO.getRequestDaysCount());
        holidayRequest = holidayRequestRepository.save(holidayRequest);
        System.out.println("holidayRequest save success! requestId:{}" + holidayRequest.getRequestId());

        // 2. query task list, then complete
        // todo 这里查询结果打印出来...
        completeTask(businessKey, null);
    }


    public void approve(ApproveOrRejectRequestDTO approveOrRejectRequestDTO) {
        // 1. query holiday request
        Optional<HolidayRequest> holidayRequestOptional = holidayRequestRepository.findByRequestId(approveOrRejectRequestDTO.getHolidayRequestId());
        if (!holidayRequestOptional.isPresent()) {
            // 查不到请假记录，直接返回（也可做error提示）
            return;
        }
        HolidayRequest holidayRequest = holidayRequestOptional.get();

        // 2.update holiday request status
        holidayRequest.setBusinessKey(approveOrRejectRequestDTO.getBusinessKey());
        holidayRequest.setHolidayStage(approveOrRejectRequestDTO.getHolidayStage());
        holidayRequest.setHolidayStatus(new HolidayStatus(approveOrRejectRequestDTO.getHolidayStatusId()));
        holidayRequestRepository.save(holidayRequest);

        // 3.approve or deny
        JSONObject variables = new JSONObject();
        JSONObject mainVariable = new JSONObject();
        JSONObject trueValue = new JSONObject();
        trueValue.put("value", true);
        JSONObject falseValue = new JSONObject();
        falseValue.put("value", false);
        // for test type
        if (approveOrRejectRequestDTO.getHolidayStatusId() == 2) {
            variables.put("approved", trueValue);
            variables.put("isFurtherApprovelRequired", falseValue);
        } else if (approveOrRejectRequestDTO.getHolidayStatusId() == 4) {
            variables.put("approved", trueValue);
            variables.put("isFurtherApprovelRequired", trueValue);
        } else if (approveOrRejectRequestDTO.getHolidayStatusId() == 3) {
            variables.put("approved", falseValue);
        }
        mainVariable.put("variables", variables);
        completeTask(approveOrRejectRequestDTO.getBusinessKey(), mainVariable);
    }


    public String getCurrentTaskIdByBusinessKey(String businessKey) {
        String url = activeTaskUrl + businessKey;
        HttpEntity<String> entity = new HttpEntity<>(headers());
        // send http request for holiday request, to get task list
        ResponseEntity<ProcessTaskResponseDTO[]> result =
                restTemplate.exchange(url, HttpMethod.GET, entity, ProcessTaskResponseDTO[].class);
        System.out.println("当前任务列表：" + result);
        return Objects.requireNonNull(result.getBody())[0].getId();
    }

    public void completeTask(String businessKey, JSONObject variables) {
        String url = completeTaskUrl.replace("$TASK_ID", getCurrentTaskIdByBusinessKey(businessKey));
        HttpEntity<String> entity;
        if (null == variables) {
            entity = new HttpEntity<>(headers());
        } else {
            entity = new HttpEntity<>(variables.toString(), headers());
        }
        System.out.println("entity===>" + entity);
        restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
    }
}
