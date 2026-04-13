package com.example.EmployeeApiCrud.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "builder_rm_details")
public class BuilderRmDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String fullName;
    private String designation;
    private String mobileNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthdayDate;

    private String spouseFullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date spouseBirthdayDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date marriageAnniversaryDate;

    private String firstChildFullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date firstChildBirthdayDate;

    private String secondChildFullName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date secondChildBirthdayDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date")
    private final Date createdDate = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public Date getBirthdayDate() { return birthdayDate; }
    public void setBirthdayDate(Date birthdayDate) { this.birthdayDate = birthdayDate; }
    public String getSpouseFullName() { return spouseFullName; }
    public void setSpouseFullName(String spouseFullName) { this.spouseFullName = spouseFullName; }
    public Date getSpouseBirthdayDate() { return spouseBirthdayDate; }
    public void setSpouseBirthdayDate(Date spouseBirthdayDate) { this.spouseBirthdayDate = spouseBirthdayDate; }
    public Date getMarriageAnniversaryDate() { return marriageAnniversaryDate; }
    public void setMarriageAnniversaryDate(Date marriageAnniversaryDate) { this.marriageAnniversaryDate = marriageAnniversaryDate; }
    public String getFirstChildFullName() { return firstChildFullName; }
    public void setFirstChildFullName(String firstChildFullName) { this.firstChildFullName = firstChildFullName; }
    public Date getFirstChildBirthdayDate() { return firstChildBirthdayDate; }
    public void setFirstChildBirthdayDate(Date firstChildBirthdayDate) { this.firstChildBirthdayDate = firstChildBirthdayDate; }
    public String getSecondChildFullName() { return secondChildFullName; }
    public void setSecondChildFullName(String secondChildFullName) { this.secondChildFullName = secondChildFullName; }
    public Date getSecondChildBirthdayDate() { return secondChildBirthdayDate; }
    public void setSecondChildBirthdayDate(Date secondChildBirthdayDate) { this.secondChildBirthdayDate = secondChildBirthdayDate; }
    public Date getCreatedDate() { return createdDate; }
}
