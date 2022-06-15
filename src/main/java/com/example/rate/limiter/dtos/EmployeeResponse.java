package com.example.rate.limiter.dtos;

import com.example.rate.limiter.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {
  private ResponseStatus status;
  private String message;
  private EmployeeDetail employeeDetail;
}
