package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.ProjectRegistration;
import com.example.EmployeeApiCrud.repository.ProjectRegistrationRepo;
import com.example.EmployeeApiCrud.service.ProjectRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProjectRegistrationServiceImpl implements ProjectRegistrationService {

    @Autowired
    private ProjectRegistrationRepo projectRegistrationRepo;

    @Override
    public ProjectRegistration createProjectRegistration(
            ProjectRegistration project,
            MultipartFile projectBrochure,
            MultipartFile reraAttachment,
            MultipartFile reraCertificate,
            MultipartFile gstCertificate
    ) {
        if (projectBrochure != null && !projectBrochure.isEmpty())
            project.setProjectBrochure(saveFile(projectBrochure));
        if (reraAttachment != null && !reraAttachment.isEmpty())
            project.setReraAttachment(saveFile(reraAttachment));
        if (reraCertificate != null && !reraCertificate.isEmpty())
            project.setReraCertificate(saveFile(reraCertificate));
        if (gstCertificate != null && !gstCertificate.isEmpty())
            project.setGstCertificate(saveFile(gstCertificate));
        return projectRegistrationRepo.save(project);
    }

    @Override
    public ProjectRegistration updateProjectRegistration(
            Long id,
            ProjectRegistration project,
            MultipartFile projectBrochure,
            MultipartFile reraAttachment,
            MultipartFile reraCertificate,
            MultipartFile gstCertificate
    ) {
        ProjectRegistration existing = projectRegistrationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        existing.setDeveloperName(project.getDeveloperName());
        existing.setPromoterName(project.getPromoterName());
        existing.setProjectName(project.getProjectName());
        existing.setProjectAddress(project.getProjectAddress());
        existing.setHasRera(project.isHasRera());
        existing.setManagerName(project.getManagerName());
        existing.setManagerMobileNo(project.getManagerMobileNo());
        existing.setManagerEmail(project.getManagerEmail());
        existing.setBillingPersonName(project.getBillingPersonName());
        existing.setBillingPersonMobileNo(project.getBillingPersonMobileNo());
        existing.setBillingPersonEmail(project.getBillingPersonEmail());

        if (projectBrochure != null && !projectBrochure.isEmpty())
            existing.setProjectBrochure(saveFile(projectBrochure));
        if (reraAttachment != null && !reraAttachment.isEmpty())
            existing.setReraAttachment(saveFile(reraAttachment));
        if (reraCertificate != null && !reraCertificate.isEmpty())
            existing.setReraCertificate(saveFile(reraCertificate));
        if (gstCertificate != null && !gstCertificate.isEmpty())
            existing.setGstCertificate(saveFile(gstCertificate));

        return projectRegistrationRepo.save(existing);
    }

    @Override
    public ProjectRegistration getProjectRegistrationById(Long id) {
        return projectRegistrationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Override
    public List<ProjectRegistration> getAll() {
        return projectRegistrationRepo.findAll();
    }

    @Override
    public void deleteProjectRegistration(Long id) {
        if (!projectRegistrationRepo.existsById(id))
            throw new RuntimeException("Project not found with id: " + id);
        projectRegistrationRepo.deleteById(id);
    }

    private String saveFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") &&
                    !contentType.equals("image/png") &&
                    !contentType.equals("application/pdf") &&
                    !contentType.equals("application/vnd.ms-powerpoint") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))) {
                throw new RuntimeException("Only JPG, PNG, PDF, and PPT files are allowed");
            }

            String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = System.currentTimeMillis() + "_" + originalName;
            Path path = Paths.get("uploads/projects/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return "uploads/projects/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
