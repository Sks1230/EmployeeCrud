package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.HrAlert;
import com.example.EmployeeApiCrud.dto.AnalyticsReport;

import java.util.List;

public interface AnalyticsService {

    // --- Salary Monitoring ---
    AnalyticsReport getLowSalaryEmployees(float threshold);
    AnalyticsReport getHighSalaryAnomalies();
    AnalyticsReport getSalarySpikes(int days);
    AnalyticsReport getEmployeeSalaryGrowth(Long empId);
    AnalyticsReport getOverallSalaryTrend();

    // --- Employee Risk Analysis ---
    AnalyticsReport getUnderpaidEmployees();
    AnalyticsReport getBestPerformers(int topN);
    AnalyticsReport getRiskyEmployees();

    // --- Data Quality ---
    AnalyticsReport getDuplicateRecords();
    AnalyticsReport getMissingDataEmployees();

    // --- Reports ---
    AnalyticsReport generateMonthlyReport();
    AnalyticsReport generateWeeklyAnalytics();

    // --- System Health ---
    AnalyticsReport getSystemHealth();

    // --- Hiring ---
    AnalyticsReport getHiringTrend();

    // --- Alerts ---
    List<HrAlert> getAllAlerts();
    List<HrAlert> getUnresolvedAlerts();
    HrAlert resolveAlert(Long alertId);

    // --- Monitoring Runner ---
    void runAllChecks();
}
