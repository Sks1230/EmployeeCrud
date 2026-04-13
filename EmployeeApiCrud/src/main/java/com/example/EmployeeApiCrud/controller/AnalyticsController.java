package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.HrAlert;
import com.example.EmployeeApiCrud.dto.AnalyticsReport;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // ---------------------------------------------------------------- SALARY MONITORING

    /** Low salary employees. Default threshold = 20,000 */
    @GetMapping("/salary/low")
    public ResponseEntity<APIResponse> getLowSalary(
            @RequestParam(defaultValue = "20000") float threshold) {
        AnalyticsReport r = analyticsService.getLowSalaryEmployees(threshold);
        return ResponseEntity.ok(new APIResponse(200, "Low salary employees", r));
    }

    /** Statistical salary anomalies (Z-score > 2) */
    @GetMapping("/salary/anomalies")
    public ResponseEntity<APIResponse> getSalaryAnomalies() {
        AnalyticsReport r = analyticsService.getHighSalaryAnomalies();
        return ResponseEntity.ok(new APIResponse(200, "Salary anomalies detected", r));
    }

    /** Salary spikes (>30% change). Default window = last 30 days */
    @GetMapping("/salary/spikes")
    public ResponseEntity<APIResponse> getSalarySpikes(
            @RequestParam(defaultValue = "30") int days) {
        AnalyticsReport r = analyticsService.getSalarySpikes(days);
        return ResponseEntity.ok(new APIResponse(200, "Salary spikes in last " + days + " days", r));
    }

    /** Full salary change history + growth % for one employee */
    @GetMapping("/salary/growth/{empId}")
    public ResponseEntity<APIResponse> getSalaryGrowth(@PathVariable Long empId) {
        AnalyticsReport r = analyticsService.getEmployeeSalaryGrowth(empId);
        return ResponseEntity.ok(new APIResponse(200, "Employee salary growth", r));
    }

    /** Month-by-month salary trend across all employees */
    @GetMapping("/salary/trend")
    public ResponseEntity<APIResponse> getSalaryTrend() {
        AnalyticsReport r = analyticsService.getOverallSalaryTrend();
        return ResponseEntity.ok(new APIResponse(200, "Overall salary trend", r));
    }

    // ---------------------------------------------------------------- EMPLOYEE RISK

    /** Employees in the bottom 25th salary percentile */
    @GetMapping("/employees/underpaid")
    public ResponseEntity<APIResponse> getUnderpaid() {
        AnalyticsReport r = analyticsService.getUnderpaidEmployees();
        return ResponseEntity.ok(new APIResponse(200, "Underpaid employees", r));
    }

    /** Top N earners — proxy for best performers. Default topN = 10 */
    @GetMapping("/employees/best-performers")
    public ResponseEntity<APIResponse> getBestPerformers(
            @RequestParam(defaultValue = "10") int topN) {
        AnalyticsReport r = analyticsService.getBestPerformers(topN);
        return ResponseEntity.ok(new APIResponse(200, "Best performers (top " + topN + ")", r));
    }

    /** Employees with HIGH or MEDIUM resignation risk score */
    @GetMapping("/employees/risky")
    public ResponseEntity<APIResponse> getRiskyEmployees() {
        AnalyticsReport r = analyticsService.getRiskyEmployees();
        return ResponseEntity.ok(new APIResponse(200, "Risky employees — resignation risk analysis", r));
    }

    // ---------------------------------------------------------------- DATA QUALITY

    /** Employees with duplicate names */
    @GetMapping("/employees/duplicates")
    public ResponseEntity<APIResponse> getDuplicates() {
        AnalyticsReport r = analyticsService.getDuplicateRecords();
        return ResponseEntity.ok(new APIResponse(200, "Duplicate employee records", r));
    }

    /** Employees with null / empty / zero fields */
    @GetMapping("/employees/missing-data")
    public ResponseEntity<APIResponse> getMissingData() {
        AnalyticsReport r = analyticsService.getMissingDataEmployees();
        return ResponseEntity.ok(new APIResponse(200, "Employees with missing data", r));
    }

    // ---------------------------------------------------------------- REPORTS

    /** Full monthly HR report with city-wise payroll breakdown */
    @GetMapping("/reports/monthly")
    public ResponseEntity<APIResponse> getMonthlyReport() {
        AnalyticsReport r = analyticsService.generateMonthlyReport();
        return ResponseEntity.ok(new APIResponse(200, "Monthly HR report", r));
    }

    /** Weekly summary: salary changes, alerts, low-salary count */
    @GetMapping("/reports/weekly")
    public ResponseEntity<APIResponse> getWeeklyReport() {
        AnalyticsReport r = analyticsService.generateWeeklyAnalytics();
        return ResponseEntity.ok(new APIResponse(200, "Weekly analytics report", r));
    }

    // ---------------------------------------------------------------- SYSTEM HEALTH

    /** Overall system health: HEALTHY / WARNING / CRITICAL */
    @GetMapping("/system/health")
    public ResponseEntity<APIResponse> getSystemHealth() {
        AnalyticsReport r = analyticsService.getSystemHealth();
        return ResponseEntity.ok(new APIResponse(200, "System health status", r));
    }

    // ---------------------------------------------------------------- HIRING TREND

    /** Employee distribution by city, age group, and salary band */
    @GetMapping("/hiring/trend")
    public ResponseEntity<APIResponse> getHiringTrend() {
        AnalyticsReport r = analyticsService.getHiringTrend();
        return ResponseEntity.ok(new APIResponse(200, "Hiring trend analysis", r));
    }

    // ---------------------------------------------------------------- HR ALERTS

    /** All HR alerts (newest first) */
    @GetMapping("/alerts")
    public ResponseEntity<APIResponse> getAllAlerts() {
        List<HrAlert> alerts = analyticsService.getAllAlerts();
        return ResponseEntity.ok(new APIResponse(200, "All HR alerts (" + alerts.size() + ")", alerts));
    }

    /** Only unresolved / active alerts */
    @GetMapping("/alerts/unresolved")
    public ResponseEntity<APIResponse> getUnresolvedAlerts() {
        List<HrAlert> alerts = analyticsService.getUnresolvedAlerts();
        return ResponseEntity.ok(new APIResponse(200, "Unresolved alerts (" + alerts.size() + ")", alerts));
    }

    /** Mark an alert as resolved */
    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<APIResponse> resolveAlert(@PathVariable Long id) {
        HrAlert alert = analyticsService.resolveAlert(id);
        return ResponseEntity.ok(new APIResponse(200, "Alert resolved", alert));
    }

    // ---------------------------------------------------------------- MANUAL TRIGGER

    /** Manually trigger all monitoring checks (also runs on schedule) */
    @PostMapping("/run-checks")
    public ResponseEntity<APIResponse> runChecks() {
        analyticsService.runAllChecks();
        return ResponseEntity.ok(new APIResponse(200, "All HR monitoring checks executed", null));
    }
}
