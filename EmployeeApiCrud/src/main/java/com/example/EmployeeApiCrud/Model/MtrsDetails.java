package com.example.EmployeeApiCrud.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "mtrs_details")
public class MtrsDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mtrsId;
    private String mainTeam;
    private String subTeam;
    private String builder;
    private String project;
    private String client;
    private String unitNo;
    private String area;
    private String uploadFile;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_date")
    private final Date createdDate = new Date();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMtrsId() { return mtrsId; }
    public void setMtrsId(String mtrsId) { this.mtrsId = mtrsId; }
    public String getMainTeam() { return mainTeam; }
    public void setMainTeam(String mainTeam) { this.mainTeam = mainTeam; }
    public String getSubTeam() { return subTeam; }
    public void setSubTeam(String subTeam) { this.subTeam = subTeam; }
    public String getBuilder() { return builder; }
    public void setBuilder(String builder) { this.builder = builder; }
    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }
    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }
    public String getUnitNo() { return unitNo; }
    public void setUnitNo(String unitNo) { this.unitNo = unitNo; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getUploadFile() { return uploadFile; }
    public void setUploadFile(String uploadFile) { this.uploadFile = uploadFile; }
    public Date getCreatedDate() { return createdDate; }
}
