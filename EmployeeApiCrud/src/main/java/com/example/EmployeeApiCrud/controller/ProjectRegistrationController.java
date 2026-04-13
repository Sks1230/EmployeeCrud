package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.ProjectRegistration;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.ProjectRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
public class ProjectRegistrationController {

    @Autowired
    private ProjectRegistrationService projectRegistrationService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> create(
            @RequestParam(required = false) String developerName,
            @RequestParam(required = false) String promoterName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String projectAddress,
            @RequestParam(required = false) boolean hasRera,
            @RequestParam(required = false) String managerName,
            @RequestParam(required = false) String managerMobileNo,
            @RequestParam(required = false) String managerEmail,
            @RequestParam(required = false) String billingPersonName,
            @RequestParam(required = false) String billingPersonMobileNo,
            @RequestParam(required = false) String billingPersonEmail,
            @RequestParam(required = false) MultipartFile projectBrochure,
            @RequestParam(required = false) MultipartFile reraAttachment,
            @RequestParam(required = false) MultipartFile reraCertificate,
            @RequestParam(required = false) MultipartFile gstCertificate
    ) {
        try {
            ProjectRegistration project = buildProject(developerName, promoterName, projectName,
                    projectAddress, hasRera, managerName, managerMobileNo, managerEmail,
                    billingPersonName, billingPersonMobileNo, billingPersonEmail);

            return ResponseEntity.ok(new APIResponse(1, "Project created successfully",
                    projectRegistrationService.createProjectRegistration(project, projectBrochure, reraAttachment, reraCertificate, gstCertificate)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> update(
            @PathVariable Long id,
            @RequestParam(required = false) String developerName,
            @RequestParam(required = false) String promoterName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String projectAddress,
            @RequestParam(required = false) boolean hasRera,
            @RequestParam(required = false) String managerName,
            @RequestParam(required = false) String managerMobileNo,
            @RequestParam(required = false) String managerEmail,
            @RequestParam(required = false) String billingPersonName,
            @RequestParam(required = false) String billingPersonMobileNo,
            @RequestParam(required = false) String billingPersonEmail,
            @RequestParam(required = false) MultipartFile projectBrochure,
            @RequestParam(required = false) MultipartFile reraAttachment,
            @RequestParam(required = false) MultipartFile reraCertificate,
            @RequestParam(required = false) MultipartFile gstCertificate
    ) {
        try {
            ProjectRegistration project = buildProject(developerName, promoterName, projectName,
                    projectAddress, hasRera, managerName, managerMobileNo, managerEmail,
                    billingPersonName, billingPersonMobileNo, billingPersonEmail);

            return ResponseEntity.ok(new APIResponse(1, "Project updated successfully",
                    projectRegistrationService.updateProjectRegistration(id, project, projectBrochure, reraAttachment, reraCertificate, gstCertificate)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "Project fetched successfully",
                    projectRegistrationService.getProjectRegistrationById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAll() {
        try {
            return ResponseEntity.ok(new APIResponse(1, "All projects fetched successfully",
                    projectRegistrationService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable Long id) {
        try {
            projectRegistrationService.deleteProjectRegistration(id);
            return ResponseEntity.ok(new APIResponse(1, "Project deleted successfully with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    private ProjectRegistration buildProject(String developerName, String promoterName, String projectName,
                                              String projectAddress, boolean hasRera, String managerName,
                                              String managerMobileNo, String managerEmail, String billingPersonName,
                                              String billingPersonMobileNo, String billingPersonEmail) {
        ProjectRegistration project = new ProjectRegistration();
        project.setDeveloperName(developerName);
        project.setPromoterName(promoterName);
        project.setProjectName(projectName);
        project.setProjectAddress(projectAddress);
        project.setHasRera(hasRera);
        project.setManagerName(managerName);
        project.setManagerMobileNo(managerMobileNo);
        project.setManagerEmail(managerEmail);
        project.setBillingPersonName(billingPersonName);
        project.setBillingPersonMobileNo(billingPersonMobileNo);
        project.setBillingPersonEmail(billingPersonEmail);
        return project;
    }
}
