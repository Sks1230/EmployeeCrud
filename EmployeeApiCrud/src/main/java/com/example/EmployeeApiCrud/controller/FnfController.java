package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.Fnf;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.FnfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fnf")
public class FnfController {

    @Autowired
    private FnfService fnfService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> create(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String hodName,
            @RequestParam(required = false) Double percentage,
            @RequestParam(required = false) String remarks
    ) {
        try {
            Fnf fnf = build(code, name, hodName, percentage, remarks);
            return ResponseEntity.ok(new APIResponse(1, "FNF record created successfully",
                    fnfService.create(fnf)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> update(
            @PathVariable Long id,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String hodName,
            @RequestParam(required = false) Double percentage,
            @RequestParam(required = false) String remarks
    ) {
        try {
            Fnf fnf = build(code, name, hodName, percentage, remarks);
            return ResponseEntity.ok(new APIResponse(1, "FNF record updated successfully",
                    fnfService.update(id, fnf)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "FNF record fetched successfully",
                    fnfService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAll() {
        try {
            return ResponseEntity.ok(new APIResponse(1, "All FNF records fetched successfully",
                    fnfService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable Long id) {
        try {
            fnfService.delete(id);
            return ResponseEntity.ok(new APIResponse(1, "FNF record deleted successfully with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    private Fnf build(String code, String name, String hodName, Double percentage, String remarks) {
        Fnf fnf = new Fnf();
        fnf.setCode(code);
        fnf.setName(name);
        fnf.setHodName(hodName);
        fnf.setPercentage(percentage);
        fnf.setRemarks(remarks);
        return fnf;
    }
}
