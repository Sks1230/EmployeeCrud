package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.dto.EmpRequest;

import java.util.List;

public interface EmpService {

    Employee createEmployee(EmpRequest empRequest);

    Employee updateEmployee(Long empId, EmpRequest empRequest);

    Employee getEmployeeById(Long empId);

    List<Employee> getAllEmployees();

    void deleteEmployee(Long empId);

    Employee login(String empCode, String password);

    void updatePassword(Long empId, String currentPassword, String newPassword);

    Employee updateProfile(Long empId, EmpRequest empRequest);
}
