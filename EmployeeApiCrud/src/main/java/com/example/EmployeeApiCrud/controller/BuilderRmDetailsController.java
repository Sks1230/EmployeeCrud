package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.BuilderRmDetails;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.BuilderRmDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/builder-rm")
public class BuilderRmDetailsController {

    @Autowired
    private BuilderRmDetailsService builderRmDetailsService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> create(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthdayDate,
            @RequestParam(required = false) String spouseFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date spouseBirthdayDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date marriageAnniversaryDate,
            @RequestParam(required = false) String firstChildFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date firstChildBirthdayDate,
            @RequestParam(required = false) String secondChildFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date secondChildBirthdayDate
    ) {
        try {
            BuilderRmDetails obj = build(companyName, fullName, designation, mobileNo, birthdayDate,
                    spouseFullName, spouseBirthdayDate, marriageAnniversaryDate,
                    firstChildFullName, firstChildBirthdayDate, secondChildFullName, secondChildBirthdayDate);
            return ResponseEntity.ok(new APIResponse(1, "Builder RM created successfully",
                    builderRmDetailsService.create(obj)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> update(
            @PathVariable Long id,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) String mobileNo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthdayDate,
            @RequestParam(required = false) String spouseFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date spouseBirthdayDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date marriageAnniversaryDate,
            @RequestParam(required = false) String firstChildFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date firstChildBirthdayDate,
            @RequestParam(required = false) String secondChildFullName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date secondChildBirthdayDate
    ) {
        try {
            BuilderRmDetails obj = build(companyName, fullName, designation, mobileNo, birthdayDate,
                    spouseFullName, spouseBirthdayDate, marriageAnniversaryDate,
                    firstChildFullName, firstChildBirthdayDate, secondChildFullName, secondChildBirthdayDate);
            return ResponseEntity.ok(new APIResponse(1, "Builder RM updated successfully",
                    builderRmDetailsService.update(id, obj)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Builder RM fetched successfully",
                    builderRmDetailsService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAll() {
        try {
            return ResponseEntity.ok(new APIResponse(1, "All Builder RMs fetched successfully",
                    builderRmDetailsService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable Long id) {
        try {
            builderRmDetailsService.delete(id);
            return ResponseEntity.ok(new APIResponse(1, "Builder RM deleted successfully with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    private BuilderRmDetails build(String companyName, String fullName, String designation, String mobileNo,
                                    Date birthdayDate, String spouseFullName, Date spouseBirthdayDate,
                                    Date marriageAnniversaryDate, String firstChildFullName,
                                    Date firstChildBirthdayDate, String secondChildFullName, Date secondChildBirthdayDate) {
        BuilderRmDetails obj = new BuilderRmDetails();
        obj.setCompanyName(companyName);
        obj.setFullName(fullName);
        obj.setDesignation(designation);
        obj.setMobileNo(mobileNo);
        obj.setBirthdayDate(birthdayDate);
        obj.setSpouseFullName(spouseFullName);
        obj.setSpouseBirthdayDate(spouseBirthdayDate);
        obj.setMarriageAnniversaryDate(marriageAnniversaryDate);
        obj.setFirstChildFullName(firstChildFullName);
        obj.setFirstChildBirthdayDate(firstChildBirthdayDate);
        obj.setSecondChildFullName(secondChildFullName);
        obj.setSecondChildBirthdayDate(secondChildBirthdayDate);
        return obj;
    }
}
