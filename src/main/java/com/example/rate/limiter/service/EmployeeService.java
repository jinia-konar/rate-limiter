package com.example.rate.limiter.service;

import com.example.rate.limiter.dtos.EmployeeDetail;
import com.example.rate.limiter.dtos.EmployeeResponse;

public interface EmployeeService {
   EmployeeResponse addEmployee(EmployeeDetail employeeDetail);
   EmployeeResponse fetchEmployee(Long id);
}
