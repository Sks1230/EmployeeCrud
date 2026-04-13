package com.example.EmployeeApiCrud.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hr_alert")
public class HrAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LOW_SALARY | SALARY_ANOMALY | SALARY_SPIKE | RESIGNATION_RISK |
    // DUPLICATE_RECORD | MISSING_DATA | PERFORMANCE_DROP | UNUSUAL_ACTIVITY
    @Column(name = "alert_type", nullable = false)
    private String alertType;

    // LOW | MEDIUM | HIGH | CRITICAL
    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "emp_code")
    private String empCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_resolved")
    private boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
