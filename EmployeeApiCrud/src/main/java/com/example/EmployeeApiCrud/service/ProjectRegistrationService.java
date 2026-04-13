package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.ProjectRegistration;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectRegistrationService {

    ProjectRegistration createProjectRegistration(
            ProjectRegistration project,
            MultipartFile projectBrochure,
            MultipartFile reraAttachment,
            MultipartFile reraCertificate,
            MultipartFile gstCertificate
    );

    ProjectRegistration updateProjectRegistration(
            Long id,
            ProjectRegistration project,
            MultipartFile projectBrochure,
            MultipartFile reraAttachment,
            MultipartFile reraCertificate,
            MultipartFile gstCertificate
    );

    ProjectRegistration getProjectRegistrationById(Long id);

    List<ProjectRegistration> getAll();

    void deleteProjectRegistration(Long id);
}
