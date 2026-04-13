package com.example.EmployeeApiCrud.dto;

import com.example.EmployeeApiCrud.Model.Asset;

public class AssetHistoryResponseBody {

    private String type;
    private String emp_code;
    private String serialNumber;
    private String assetStatus;
    private String branch;
    private String department;
    private String remarks;

    public AssetHistoryResponseBody(Asset asset) {

        this.type = asset.getType();
        this.emp_code = asset.getEmp_code();
        this.serialNumber = asset.getSerialNumber();
        this.assetStatus = asset.getAssetStatus();
        this.branch = asset.getBranch();
        this.department = asset.getDepartment();
        this.remarks = asset.getRemarks();
    }

    public String getType() {
        return type;
    }

    public String getEmp_code() {
        return emp_code;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public String getBranch() {
        return branch;
    }

    public String getDepartment() {
        return department;
    }

    public String getRemarks() {
        return remarks;
    }
}