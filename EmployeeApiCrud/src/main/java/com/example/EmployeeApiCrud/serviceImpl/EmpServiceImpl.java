package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.Model.SalaryHistory;
import com.example.EmployeeApiCrud.dto.EmpRequest;
import com.example.EmployeeApiCrud.repository.EmployeeRepository;
import com.example.EmployeeApiCrud.repository.SalaryHistoryRepository;
import com.example.EmployeeApiCrud.service.EmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmpServiceImpl implements EmpService {

    private static final Logger log = LoggerFactory.getLogger(EmpServiceImpl.class);
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryHistoryRepository salaryHistoryRepository;

    @Override
    public Employee createEmployee(EmpRequest empRequest) {
        if (empRequest.getEmpCode() != null && empRequest.getEmpCode().length() != 4) {
            throw new RuntimeException("Emp Code must be exactly 4 digits");
        }
        Employee employee = new Employee();
        employee.setEmp_name(empRequest.getEmp_name());
        employee.setEmp_salary(empRequest.getEmp_salary());
        employee.setEmp_age(empRequest.getEmp_age());
        employee.setEmp_city(empRequest.getEmp_city());
        employee.setEmpCode(empRequest.getEmpCode());
        if (empRequest.getPassword() != null && !empRequest.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(empRequest.getPassword()));
        }
        Employee saved = employeeRepository.save(employee);
        // Record initial salary as first history entry
        if (saved.getEmp_salary() != null && saved.getEmp_salary() > 0) {
            recordSalaryHistory(saved, 0f, saved.getEmp_salary(), "SYSTEM_CREATE");
        }
        log.info("Employee created with id={}", saved.getEmpid());
        return saved;
    }

    @Override
    public Employee updateEmployee(Long empId, EmpRequest empRequest) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(empId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            if (empRequest.getEmpCode() != null && empRequest.getEmpCode().length() != 4) {
                throw new RuntimeException("Emp Code must be exactly 4 digits");
            }
            Float oldSalary = employee.getEmp_salary();
            employee.setEmp_name(empRequest.getEmp_name());
            employee.setEmp_salary(empRequest.getEmp_salary());
            employee.setEmp_age(empRequest.getEmp_age());
            employee.setEmp_city(empRequest.getEmp_city());
            employee.setEmpCode(empRequest.getEmpCode());
            Employee saved = employeeRepository.save(employee);
            // Track salary change
            if (empRequest.getEmp_salary() != null && !empRequest.getEmp_salary().equals(oldSalary)) {
                recordSalaryHistory(saved, oldSalary, empRequest.getEmp_salary(), "HR_UPDATE");
            }
            log.info("Employee updated with id={}", saved.getEmpid());
            return saved;
        } else {
            throw new RuntimeException("Employee not found with ID: " + empId);
        }
    }

    @Override
    public Employee getEmployeeById(Long empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public void deleteEmployee(Long empId) {
        if (employeeRepository.existsById(empId)) {
            employeeRepository.deleteById(empId);
            log.info("Employee deleted with id={}", empId);
        } else {
            throw new RuntimeException("Employee not found with ID: " + empId);
        }
    }

    @Override
    public Employee login(String empCode, String password) {
        Employee employee = employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Invalid emp code or password"));
        if (employee.getPassword() == null || !passwordEncoder.matches(password, employee.getPassword())) {
            throw new RuntimeException("Invalid emp code or password");
        }
        return employee;
    }

    @Override
    public Employee updateProfile(Long empId, EmpRequest empRequest) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
        Float oldSalary = employee.getEmp_salary();
        employee.setEmp_name(empRequest.getEmp_name());
        employee.setEmp_salary(empRequest.getEmp_salary());
        employee.setEmp_age(empRequest.getEmp_age());
        employee.setEmp_city(empRequest.getEmp_city());
        Employee saved = employeeRepository.save(employee);
        if (empRequest.getEmp_salary() != null && !empRequest.getEmp_salary().equals(oldSalary)) {
            recordSalaryHistory(saved, oldSalary, empRequest.getEmp_salary(), "PROFILE_UPDATE");
        }
        log.info("Profile updated for employee id={}", saved.getEmpid());
        return saved;
    }

    private void recordSalaryHistory(Employee employee, Float oldSalary, Float newSalary, String changedBy) {
        SalaryHistory sh = new SalaryHistory();
        sh.setEmployee(employee);
        sh.setOldSalary(oldSalary);
        sh.setNewSalary(newSalary);
        sh.setChangedAt(LocalDateTime.now());
        sh.setChangedBy(changedBy);
        salaryHistoryRepository.save(sh);
        log.info("SalaryHistory recorded: empId={} {} → {} by {}", employee.getEmpid(), oldSalary, newSalary, changedBy);
    }

    @Override
    public void updatePassword(Long empId, String currentPassword, String newPassword) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!passwordEncoder.matches(currentPassword, employee.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
        log.info("Password updated for employee id={}", empId);
    }
}
