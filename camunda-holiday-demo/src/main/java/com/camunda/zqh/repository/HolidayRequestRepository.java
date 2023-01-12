package com.camunda.zqh.repository;

import com.camunda.zqh.model.HolidayRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HolidayRequestRepository {

    public HolidayRequest save(HolidayRequest holidayRequest) {
        // mock id
        holidayRequest.setRequestId(234L);
        return holidayRequest;
    }

    public Optional<HolidayRequest> findByRequestId(Long requestId) {
        // mock holiday request
        HolidayRequest holidayRequest = new HolidayRequest();
        holidayRequest.setRequestId(requestId);
        return Optional.of(holidayRequest);
    }

}
