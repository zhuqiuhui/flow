package com.camunda.zqh.controller;


import com.camunda.zqh.dto.ApproveOrRejectRequestDTO;
import com.camunda.zqh.dto.StudentHolidayRequestDTO;
import com.camunda.zqh.service.CustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("camunda/flow")
public class ProcessController {

    @Autowired
    private CustomService customService;

    /**
     * 发起请假请求
     * @param businessProcessKey 业务key，任意传即可
     * @param studentHolidayRequestDTO 请假请求
     * @return 请假发起结果
     */
    @PostMapping("/holidayRequest/{businessProcessKey}")
    public ResponseEntity<String> processStart(@PathVariable("businessProcessKey") String businessProcessKey,
                                               @RequestBody StudentHolidayRequestDTO studentHolidayRequestDTO) {
        customService.startProcess(businessProcessKey, studentHolidayRequestDTO);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @PostMapping("/holidayRequest/approval")
    public ResponseEntity<String> approve(@RequestBody ApproveOrRejectRequestDTO approveOrRejectRequestDTO) {
        customService.approve(approveOrRejectRequestDTO);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

}
