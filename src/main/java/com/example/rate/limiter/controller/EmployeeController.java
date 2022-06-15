package com.example.rate.limiter.controller;

import com.example.rate.limiter.dtos.EmployeeDetail;
import com.example.rate.limiter.dtos.EmployeeResponse;
import com.example.rate.limiter.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee/v1")
@Slf4j
public class EmployeeController {

  @Autowired private EmployeeService service;

  @PostMapping(value = "/add_employee", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody EmployeeDetail employeeDetail) {
    return ResponseEntity.ok(service.addEmployee(employeeDetail));
  }

  /**
   * This function has a rate limit of 4 requests/minute
   * @param id
   * @return
   */
  @GetMapping(value = "/fetch_employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EmployeeResponse> fetchEmployee(@PathVariable Long id) {
    return ResponseEntity.ok(service.fetchEmployee(id));
  }
}