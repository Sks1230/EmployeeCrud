package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.dto.EmpRequest;
import com.example.EmployeeApiCrud.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmpService empService;

    @PostMapping
    public ResponseEntity<APIResponse> createEmployee(@RequestBody EmpRequest empRequest) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Employee created successfully", empService.createEmployee(empRequest)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateEmployee(@PathVariable("id") Long id, @RequestBody EmpRequest empRequest) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Employee updated successfully", empService.updateEmployee(id, empRequest)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getEmployeeById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Employee fetched successfully", empService.getEmployeeById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse> getAllEmployees() {
        try {
            return ResponseEntity.ok(new APIResponse(1, "All employees fetched successfully", empService.getAllEmployees()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<APIResponse> updateProfile(@PathVariable("id") Long id, @RequestBody EmpRequest empRequest) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Profile updated successfully", empService.updateProfile(id, empRequest)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteEmployee(@PathVariable("id") Long id) {
        try {
            empService.deleteEmployee(id);
            return ResponseEntity.ok(new APIResponse(1, "Employee deleted successfully with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }
}
