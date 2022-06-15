package com.example.rate.limiter.service.impl;

import com.example.rate.limiter.dtos.EmployeeDetail;
import com.example.rate.limiter.dtos.EmployeeResponse;
import com.example.rate.limiter.entity.EmployeeEntity;
import com.example.rate.limiter.enums.ResponseStatus;
import com.example.rate.limiter.repository.EmployeeDao;
import com.example.rate.limiter.repository.EmployeeRepository;
import com.example.rate.limiter.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired private EmployeeDao employeeDao;

  @Override
  public EmployeeResponse addEmployee(EmployeeDetail employeeDetail) {
    try {
      EmployeeEntity entity = employeeDao.saveAndFlush(EmployeeEntity.builder()
          .name(employeeDetail.getName())
          .contactNumber(employeeDetail.getContactNumber()).build());
      return EmployeeResponse.builder()
          .status(ResponseStatus.Success)
          .employeeDetail(EmployeeDetail.builder()
              .id(entity.getId().toString())
              .name(entity.getName())
              .contactNumber(entity.getContactNumber()).build()).build();
    } catch (Exception e) {
      log.error("EmployeeServiceImpl: addEmployee: failed while saving employee", e);
      return EmployeeResponse.builder()
          .status(ResponseStatus.Failure)
          .message("Unable to save employee").build();
    }
  }

  @Override
  public EmployeeResponse fetchEmployee(Long id) {
    Set<String> rateLimiterList = employeeDao.rateLimiter(id.toString());

    if (rateLimiterList.size() > 4) {
      log.info("EmployeeServiceImpl: fetchEmployee: rate limit exceeded");
      return EmployeeResponse.builder()
          .status(ResponseStatus.Failure)
          .message("Rate limit exceeded").build();
    }

    Optional<EmployeeEntity> entity = employeeDao.fetchEmployee(id);
    if (entity.isEmpty()) {
      log.info("EmployeeServiceImpl: fetchEmployee: no employee available with id {}", id);
      return EmployeeResponse.builder()
          .status(ResponseStatus.Failure)
          .message("no employee available with id " + id).build();
    }

    return EmployeeResponse.builder()
        .status(ResponseStatus.Success)
        .employeeDetail(EmployeeDetail.builder()
            .id(entity.get().getId().toString())
            .name(entity.get().getName())
            .contactNumber(entity.get().getContactNumber()).build()).build();
  }
}
