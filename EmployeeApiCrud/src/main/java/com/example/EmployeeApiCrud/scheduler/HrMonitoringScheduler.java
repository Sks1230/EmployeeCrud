package com.example.EmployeeApiCrud.scheduler;

import com.example.EmployeeApiCrud.dto.AnalyticsReport;
import com.example.EmployeeApiCrud.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HrMonitoringScheduler {

    private static final Logger log = LoggerFactory.getLogger(HrMonitoringScheduler.class);

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Hourly: run all monitoring checks.
     * Detects low salary, anomalies, spikes, resignation risk,
     * duplicates, and missing data — saves alerts to DB automatically.
     */
    @Scheduled(fixedRate = 3_600_000)
    public void hourlyMonitoring() {
        log.info("━━━ [HOURLY] HR monitoring checks started ━━━");
        try {
            analyticsService.runAllChecks();
        } catch (Exception ex) {
            log.error("[HOURLY] Monitoring error: {}", ex.getMessage(), ex);
        }
        log.info("━━━ [HOURLY] HR monitoring checks done ━━━");
    }

    /**
     * Every Monday at 09:00 — weekly analytics report logged to console.
     * Access full report via GET /analytics/reports/weekly
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void weeklyAnalytics() {
        log.info("━━━ [WEEKLY] Generating weekly analytics report ━━━");
        try {
            AnalyticsReport r = analyticsService.generateWeeklyAnalytics();
            log.info("[WEEKLY] totalEmployees={} | avgSalary={} | lowSalary={} | salaryChanges={} | newAlerts={} | unresolved={}",
                r.getSummary().get("totalEmployees"),
                r.getSummary().get("averageSalary"),
                r.getSummary().get("lowSalaryEmployees"),
                r.getSummary().get("salaryChanges"),
                r.getSummary().get("newAlertsThisWeek"),
                r.getSummary().get("unresolvedAlerts"));

            // Log risky employees in weekly window
            AnalyticsReport risky = analyticsService.getRiskyEmployees();
            log.info("[WEEKLY] Resignation risk — HIGH: {} | MEDIUM: {}",
                risky.getSummary().get("highResignRisk"),
                risky.getSummary().get("mediumResignRisk"));

        } catch (Exception ex) {
            log.error("[WEEKLY] Error generating weekly analytics: {}", ex.getMessage(), ex);
        }
    }

    /**
     * 1st of every month at 09:00 — full monthly HR report.
     * Access full report via GET /analytics/reports/monthly
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void monthlyReport() {
        log.info("━━━ [MONTHLY] Generating monthly HR report ━━━");
        try {
            AnalyticsReport r = analyticsService.generateMonthlyReport();
            log.info("[MONTHLY] month={} | employees={} | payroll={} | avgSalary={} | lowSalary={} | changes={} | alerts={}",
                r.getSummary().get("month"),
                r.getSummary().get("totalEmployees"),
                r.getSummary().get("totalPayroll"),
                r.getSummary().get("averageSalary"),
                r.getSummary().get("lowSalaryCount"),
                r.getSummary().get("salaryChangesThisMonth"),
                r.getSummary().get("unresolvedAlerts"));

            AnalyticsReport health = analyticsService.getSystemHealth();
            log.info("[MONTHLY] System health status: {}", health.getSummary().get("status"));

        } catch (Exception ex) {
            log.error("[MONTHLY] Error generating monthly report: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Daily at midnight — check for salary spikes in last 24 hours.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailySpikeCheck() {
        log.info("━━━ [DAILY] Salary spike check ━━━");
        try {
            AnalyticsReport r = analyticsService.getSalarySpikes(1);
            int spikes = r.getDetails() != null ? r.getDetails().size() : 0;
            if (spikes > 0) {
                log.warn("[DAILY] {} salary spike(s) detected today!", spikes);
                r.getDetails().forEach(spike ->
                    log.warn("  → empCode={} | {} → {} | {}% change | type={}",
                        spike.get("empCode"), spike.get("oldSalary"),
                        spike.get("newSalary"), spike.get("percentageChange"),
                        spike.get("spikeType")));
            } else {
                log.info("[DAILY] No salary spikes detected in the last 24 hours.");
            }
        } catch (Exception ex) {
            log.error("[DAILY] Spike check error: {}", ex.getMessage(), ex);
        }
    }
}
