package com.example.EmployeeApiCrud.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "project_registration")
public class ProjectRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String developerName;
    private String promoterName;
    private String projectName;
    private String projectAddress;
    private String projectBrochure;
    private boolean hasRera;
    private String reraAttachment;
    private String reraCertificate;
    private String gstCertificate;
    private String managerName;
    private String managerMobileNo;
    private String managerEmail;
    private String billingPersonName;
    private String billingPersonMobileNo;
    private String billingPersonEmail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date")
    private final Date createdDate = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeveloperName() { return developerName; }
    public void setDeveloperName(String developerName) { this.developerName = developerName; }
    public String getPromoterName() { return promoterName; }
    public void setPromoterName(String promoterName) { this.promoterName = promoterName; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getProjectAddress() { return projectAddress; }
    public void setProjectAddress(String projectAddress) { this.projectAddress = projectAddress; }
    public String getProjectBrochure() { return projectBrochure; }
    public void setProjectBrochure(String projectBrochure) { this.projectBrochure = projectBrochure; }
    public boolean isHasRera() { return hasRera; }
    public void setHasRera(boolean hasRera) { this.hasRera = hasRera; }
    public String getReraAttachment() { return reraAttachment; }
    public void setReraAttachment(String reraAttachment) { this.reraAttachment = reraAttachment; }
    public String getReraCertificate() { return reraCertificate; }
    public void setReraCertificate(String reraCertificate) { this.reraCertificate = reraCertificate; }
    public String getGstCertificate() { return gstCertificate; }
    public void setGstCertificate(String gstCertificate) { this.gstCertificate = gstCertificate; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public String getManagerMobileNo() { return managerMobileNo; }
    public void setManagerMobileNo(String managerMobileNo) { this.managerMobileNo = managerMobileNo; }
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    public String getBillingPersonName() { return billingPersonName; }
    public void setBillingPersonName(String billingPersonName) { this.billingPersonName = billingPersonName; }
    public String getBillingPersonMobileNo() { return billingPersonMobileNo; }
    public void setBillingPersonMobileNo(String billingPersonMobileNo) { this.billingPersonMobileNo = billingPersonMobileNo; }
    public String getBillingPersonEmail() { return billingPersonEmail; }
    public void setBillingPersonEmail(String billingPersonEmail) { this.billingPersonEmail = billingPersonEmail; }
    public Date getCreatedDate() { return createdDate; }
}
