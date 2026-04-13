package com.example.EmployeeApiCrud.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsReport {

    private String reportType;
    private LocalDateTime generatedAt;
    private Map<String, Object> summary;
    private List<Map<String, Object>> details;

    public AnalyticsReport(String reportType) {
        this.reportType = reportType;
        this.generatedAt = LocalDateTime.now();
    }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public Map<String, Object> getSummary() { return summary; }
    public void setSummary(Map<String, Object> summary) { this.summary = summary; }
    public List<Map<String, Object>> getDetails() { return details; }
    public void setDetails(List<Map<String, Object>> details) { this.details = details; }
}
