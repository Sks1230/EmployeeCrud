package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.Model.HrAlert;
import com.example.EmployeeApiCrud.Model.SalaryHistory;
import com.example.EmployeeApiCrud.dto.AnalyticsReport;
import com.example.EmployeeApiCrud.repository.AssetRepo;
import com.example.EmployeeApiCrud.repository.EmployeeRepository;
import com.example.EmployeeApiCrud.repository.HrAlertRepository;
import com.example.EmployeeApiCrud.repository.SalaryHistoryRepository;
import com.example.EmployeeApiCrud.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private static final float  LOW_SALARY_THRESHOLD = 20_000f;
    private static final double ANOMALY_Z_SCORE      = 2.0;
    private static final double SPIKE_PCT_THRESHOLD  = 0.30;   // 30% change
    private static final int    HIGH_RISK_SCORE       = 60;
    private static final int    MEDIUM_RISK_SCORE     = 40;

    @Autowired private EmployeeRepository      employeeRepository;
    @Autowired private SalaryHistoryRepository salaryHistoryRepository;
    @Autowired private HrAlertRepository       hrAlertRepository;
    @Autowired private AssetRepo               assetRepo;

    // ------------------------------------------------------------------ helpers

    private Map<String, Object> toMap(Employee e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("empid",   e.getEmpid());
        m.put("empCode", e.getEmpCode());
        m.put("name",    e.getEmp_name());
        m.put("salary",  e.getEmp_salary());
        m.put("age",     e.getEmp_age());
        m.put("city",    e.getEmp_city());
        return m;
    }

    private boolean hasMissingData(Employee e) {
        return (e.getEmp_name()   == null || e.getEmp_name().trim().isEmpty())
            || (e.getEmp_salary() == null || e.getEmp_salary() == 0)
            || (e.getEmp_city()   == null || e.getEmp_city().trim().isEmpty())
            ||  e.getEmp_age()    == 0;
    }

    private List<String> missingFields(Employee e) {
        List<String> fields = new ArrayList<>();
        if (e.getEmp_name()   == null || e.getEmp_name().trim().isEmpty())   fields.add("emp_name");
        if (e.getEmp_salary() == null || e.getEmp_salary() == 0)             fields.add("emp_salary");
        if (e.getEmp_city()   == null || e.getEmp_city().trim().isEmpty())   fields.add("emp_city");
        if (e.getEmp_age()    == 0)                                           fields.add("emp_age");
        return fields;
    }

    private HrAlert buildAlert(String type, String severity, String empCode, String message) {
        HrAlert a = new HrAlert();
        a.setAlertType(type);
        a.setSeverity(severity);
        a.setEmpCode(empCode);
        a.setMessage(message);
        return a;
    }

    // ============================================================ SALARY MONITORING

    @Override
    public AnalyticsReport getLowSalaryEmployees(float threshold) {
        List<Employee> all = employeeRepository.findAll();

        List<Map<String, Object>> details = all.stream()
            .filter(e -> e.getEmp_salary() != null && e.getEmp_salary() < threshold)
            .map(e -> {
                Map<String, Object> m = toMap(e);
                m.put("deficit", Math.round(threshold - e.getEmp_salary()));
                m.put("severity", e.getEmp_salary() < 10_000 ? "CRITICAL" : "HIGH");
                return m;
            })
            .sorted(Comparator.comparingDouble(m -> ((Number) m.get("salary")).doubleValue()))
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("LOW_SALARY_EMPLOYEES");
        r.setSummary(Map.of(
            "threshold",      threshold,
            "count",          details.size(),
            "totalEmployees", all.size(),
            "percentage",     all.isEmpty() ? 0 : Math.round(100.0 * details.size() / all.size())
        ));
        r.setDetails(details);
        return r;
    }

    @Override
    public AnalyticsReport getHighSalaryAnomalies() {
        List<Employee> all = employeeRepository.findAll().stream()
            .filter(e -> e.getEmp_salary() != null)
            .collect(Collectors.toList());

        if (all.size() < 3) {
            AnalyticsReport r = new AnalyticsReport("HIGH_SALARY_ANOMALIES");
            r.setSummary(Map.of("message", "Insufficient data — need at least 3 employees"));
            r.setDetails(Collections.emptyList());
            return r;
        }

        double mean   = all.stream().mapToDouble(e -> e.getEmp_salary()).average().orElse(0);
        double stddev = Math.sqrt(all.stream()
            .mapToDouble(e -> Math.pow(e.getEmp_salary() - mean, 2)).average().orElse(0));

        List<Map<String, Object>> anomalies = all.stream()
            .filter(e -> stddev > 0 && Math.abs(e.getEmp_salary() - mean) > ANOMALY_Z_SCORE * stddev)
            .map(e -> {
                double z = (e.getEmp_salary() - mean) / stddev;
                Map<String, Object> m = toMap(e);
                m.put("zScore",    Math.round(z * 100.0) / 100.0);
                m.put("deviation", Math.round(Math.abs(e.getEmp_salary() - mean)));
                m.put("type",      z > 0 ? "HIGH_OUTLIER" : "LOW_OUTLIER");
                return m;
            })
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("HIGH_SALARY_ANOMALIES");
        r.setSummary(Map.of(
            "meanSalary",        Math.round(mean),
            "standardDeviation", Math.round(stddev),
            "zScoreThreshold",   ANOMALY_Z_SCORE,
            "anomaliesFound",    anomalies.size()
        ));
        r.setDetails(anomalies);
        return r;
    }

    @Override
    public AnalyticsReport getSalarySpikes(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SalaryHistory> recent = salaryHistoryRepository.findRecentChanges(since);

        List<Map<String, Object>> spikes = recent.stream()
            .filter(sh -> sh.getOldSalary() != null && sh.getOldSalary() > 0 && sh.getNewSalary() != null)
            .filter(sh -> {
                double pct = Math.abs((sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary());
                return pct >= SPIKE_PCT_THRESHOLD;
            })
            .map(sh -> {
                double pct = (sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary() * 100;
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("empCode",           sh.getEmployee().getEmpCode());
                m.put("empName",           sh.getEmployee().getEmp_name());
                m.put("oldSalary",         sh.getOldSalary());
                m.put("newSalary",         sh.getNewSalary());
                m.put("percentageChange",  Math.round(pct * 100.0) / 100.0);
                m.put("changedAt",         sh.getChangedAt().toString());
                m.put("changedBy",         sh.getChangedBy());
                m.put("spikeType",         pct > 0 ? "INCREASE" : "DECREASE");
                return m;
            })
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("SALARY_SPIKES");
        r.setSummary(Map.of(
            "daysPeriod",    days,
            "since",         since.toString(),
            "totalChanges",  recent.size(),
            "spikesFound",   spikes.size(),
            "spikeThreshold", (int)(SPIKE_PCT_THRESHOLD * 100) + "% change"
        ));
        r.setDetails(spikes);
        return r;
    }

    @Override
    public AnalyticsReport getEmployeeSalaryGrowth(Long empId) {
        Employee emp = employeeRepository.findById(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        List<SalaryHistory> history = salaryHistoryRepository.findByEmployeeOrderByChangedAtDesc(emp);

        List<Map<String, Object>> histList = history.stream().map(sh -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("oldSalary", sh.getOldSalary());
            m.put("newSalary", sh.getNewSalary());
            m.put("change",    sh.getNewSalary() != null && sh.getOldSalary() != null
                                ? sh.getNewSalary() - sh.getOldSalary() : null);
            if (sh.getOldSalary() != null && sh.getOldSalary() > 0 && sh.getNewSalary() != null) {
                double pct = (sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary() * 100;
                m.put("percentageChange", Math.round(pct * 100.0) / 100.0);
            }
            m.put("changedAt", sh.getChangedAt() != null ? sh.getChangedAt().toString() : null);
            m.put("changedBy", sh.getChangedBy());
            return m;
        }).collect(Collectors.toList());

        // Overall growth from first recorded salary to current
        double totalGrowthPct = 0;
        if (history.size() >= 1) {
            SalaryHistory oldest = history.get(history.size() - 1);
            if (oldest.getOldSalary() != null && oldest.getOldSalary() > 0 && emp.getEmp_salary() != null) {
                totalGrowthPct = (emp.getEmp_salary() - oldest.getOldSalary()) / oldest.getOldSalary() * 100;
            }
        }

        AnalyticsReport r = new AnalyticsReport("EMPLOYEE_SALARY_GROWTH");
        r.setSummary(Map.of(
            "empCode",              emp.getEmpCode(),
            "empName",              emp.getEmp_name(),
            "currentSalary",        emp.getEmp_salary(),
            "totalChanges",         history.size(),
            "totalGrowthPct",       Math.round(totalGrowthPct * 100.0) / 100.0
        ));
        r.setDetails(histList);
        return r;
    }

    @Override
    public AnalyticsReport getOverallSalaryTrend() {
        List<SalaryHistory> allHistory = salaryHistoryRepository.findAll();

        // Monthly averages from salary history
        Map<String, DoubleSummaryStatistics> monthly = allHistory.stream()
            .filter(sh -> sh.getChangedAt() != null && sh.getNewSalary() != null)
            .collect(Collectors.groupingBy(
                sh -> sh.getChangedAt().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.summarizingDouble(sh -> sh.getNewSalary())
            ));

        List<Map<String, Object>> trend = monthly.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("month",        e.getKey());
                m.put("avgSalary",    Math.round(e.getValue().getAverage()));
                m.put("maxSalary",    Math.round(e.getValue().getMax()));
                m.put("minSalary",    Math.round(e.getValue().getMin()));
                m.put("changeCount",  e.getValue().getCount());
                return m;
            })
            .collect(Collectors.toList());

        List<Employee> allEmp = employeeRepository.findAll();
        double currentAvg = allEmp.stream().filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).average().orElse(0);
        double totalPayroll = allEmp.stream().filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).sum();

        AnalyticsReport r = new AnalyticsReport("OVERALL_SALARY_TREND");
        r.setSummary(Map.of(
            "currentAverageSalary", Math.round(currentAvg),
            "totalMonthlyPayroll",  Math.round(totalPayroll),
            "totalEmployees",       allEmp.size(),
            "monthsTracked",        trend.size(),
            "totalSalaryChanges",   allHistory.size()
        ));
        r.setDetails(trend);
        return r;
    }

    // ============================================================ EMPLOYEE RISK

    @Override
    public AnalyticsReport getUnderpaidEmployees() {
        List<Employee> all = employeeRepository.findAll().stream()
            .filter(e -> e.getEmp_salary() != null)
            .collect(Collectors.toList());

        if (all.isEmpty()) {
            AnalyticsReport r = new AnalyticsReport("UNDERPAID_EMPLOYEES");
            r.setSummary(Map.of("message", "No employees found"));
            r.setDetails(Collections.emptyList());
            return r;
        }

        List<Float> sorted = all.stream().map(Employee::getEmp_salary)
            .sorted().collect(Collectors.toList());
        float p25    = sorted.get(sorted.size() / 4);
        float median = sorted.get(sorted.size() / 2);
        double avg   = sorted.stream().mapToDouble(Float::doubleValue).average().orElse(0);

        List<Map<String, Object>> details = all.stream()
            .filter(e -> e.getEmp_salary() <= p25)
            .map(e -> {
                Map<String, Object> m = toMap(e);
                m.put("belowAvgBy",  Math.round(avg - e.getEmp_salary()));
                m.put("percentile",  "<=25th");
                return m;
            })
            .sorted(Comparator.comparingDouble(m -> ((Number) m.get("salary")).doubleValue()))
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("UNDERPAID_EMPLOYEES");
        r.setSummary(Map.of(
            "p25Salary",     p25,
            "medianSalary",  median,
            "avgSalary",     Math.round(avg),
            "underpaidCount", details.size(),
            "totalEmployees", all.size()
        ));
        r.setDetails(details);
        return r;
    }

    @Override
    public AnalyticsReport getBestPerformers(int topN) {
        List<Employee> all = employeeRepository.findAll();
        double avg = all.stream().filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).average().orElse(0);

        List<Map<String, Object>> details = all.stream()
            .filter(e -> e.getEmp_salary() != null)
            .sorted(Comparator.comparingDouble(Employee::getEmp_salary).reversed())
            .limit(topN)
            .map(e -> {
                Map<String, Object> m = toMap(e);
                m.put("aboveAvgBy",    Math.round(e.getEmp_salary() - avg));
                m.put("aboveAvgByPct", avg > 0 ? Math.round((e.getEmp_salary() - avg) / avg * 100) : 0);
                return m;
            })
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("BEST_PERFORMERS");
        r.setSummary(Map.of(
            "topN",                  topN,
            "companyAverageSalary",  Math.round(avg),
            "performersFound",       details.size()
        ));
        r.setDetails(details);
        return r;
    }

    @Override
    public AnalyticsReport getRiskyEmployees() {
        List<Employee> all = employeeRepository.findAll();
        double avg = all.stream().filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).average().orElse(0);

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<Map<String, Object>> risks = all.stream().map(e -> {
            int score = 0;
            List<String> reasons = new ArrayList<>();

            if (e.getEmp_salary() != null && e.getEmp_salary() < LOW_SALARY_THRESHOLD) {
                score += 40;
                reasons.add("Salary below 20,000 threshold");
            }
            if (e.getEmp_salary() != null && avg > 0 && e.getEmp_salary() < avg * 0.8) {
                score += 20;
                reasons.add("Salary >20% below company average");
            }

            // Salary stagnation: no raise in last 6 months
            List<SalaryHistory> hist = salaryHistoryRepository.findByEmployee(e);
            if (!hist.isEmpty()) {
                boolean hasRecentRaise = hist.stream().anyMatch(sh ->
                    sh.getChangedAt() != null
                    && sh.getChangedAt().isAfter(sixMonthsAgo)
                    && sh.getNewSalary() != null && sh.getOldSalary() != null
                    && sh.getNewSalary() > sh.getOldSalary());
                if (!hasRecentRaise) {
                    score += 20;
                    reasons.add("No salary raise in last 6 months");
                }
            }

            if (e.getEmp_age() > 55) {
                score += 10;
                reasons.add("Senior age group (>55)");
            }
            if (hasMissingData(e)) {
                score += 10;
                reasons.add("Incomplete profile data");
            }

            String level = score >= HIGH_RISK_SCORE ? "HIGH"
                         : score >= MEDIUM_RISK_SCORE ? "MEDIUM" : "LOW";

            Map<String, Object> m = toMap(e);
            m.put("riskScore", score);
            m.put("riskLevel", level);
            m.put("reasons",   reasons);
            return m;
        })
        .filter(m -> !"LOW".equals(m.get("riskLevel")))
        .sorted(Comparator.comparingInt(m -> -((Integer) m.get("riskScore"))))
        .collect(Collectors.toList());

        long high   = risks.stream().filter(m -> "HIGH".equals(m.get("riskLevel"))).count();
        long medium = risks.stream().filter(m -> "MEDIUM".equals(m.get("riskLevel"))).count();

        AnalyticsReport r = new AnalyticsReport("RISKY_EMPLOYEES");
        r.setSummary(Map.of(
            "totalEmployees",       all.size(),
            "highResignRisk",       high,
            "mediumResignRisk",     medium,
            "companyAverageSalary", Math.round(avg)
        ));
        r.setDetails(risks);
        return r;
    }

    // ============================================================ DATA QUALITY

    @Override
    public AnalyticsReport getDuplicateRecords() {
        List<Employee> all = employeeRepository.findAll();

        List<Map<String, Object>> dups = all.stream()
            .filter(e -> e.getEmp_name() != null)
            .collect(Collectors.groupingBy(e -> e.getEmp_name().trim().toLowerCase()))
            .entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .map(entry -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("duplicateName",   entry.getKey());
                m.put("occurrences",     entry.getValue().size());
                m.put("employees",       entry.getValue().stream().map(this::toMap).collect(Collectors.toList()));
                return m;
            })
            .collect(Collectors.toList());

        int dupRecords = dups.stream()
            .mapToInt(m -> ((List<?>) m.get("employees")).size()).sum();

        AnalyticsReport r = new AnalyticsReport("DUPLICATE_RECORDS");
        r.setSummary(Map.of(
            "totalEmployees",     all.size(),
            "duplicateGroups",    dups.size(),
            "totalDupRecords",    dupRecords
        ));
        r.setDetails(dups);
        return r;
    }

    @Override
    public AnalyticsReport getMissingDataEmployees() {
        List<Employee> all = employeeRepository.findAll();

        List<Map<String, Object>> details = all.stream()
            .filter(this::hasMissingData)
            .map(e -> {
                Map<String, Object> m = toMap(e);
                m.put("missingFields", missingFields(e));
                return m;
            })
            .collect(Collectors.toList());

        int quality = all.isEmpty() ? 100
            : (int) Math.round(100.0 * (all.size() - details.size()) / all.size());

        AnalyticsReport r = new AnalyticsReport("MISSING_DATA_EMPLOYEES");
        r.setSummary(Map.of(
            "totalEmployees",          all.size(),
            "incompleteRecords",       details.size(),
            "dataQualityPercentage",   quality
        ));
        r.setDetails(details);
        return r;
    }

    // ============================================================ REPORTS

    @Override
    public AnalyticsReport generateMonthlyReport() {
        LocalDateTime monthStart = LocalDateTime.now()
            .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<Employee> all = employeeRepository.findAll();
        List<SalaryHistory> monthChanges =
            salaryHistoryRepository.findByChangedAtBetween(monthStart, LocalDateTime.now());

        double avg        = all.stream().filter(e -> e.getEmp_salary() != null)
                              .mapToDouble(Employee::getEmp_salary).average().orElse(0);
        double payroll    = all.stream().filter(e -> e.getEmp_salary() != null)
                              .mapToDouble(Employee::getEmp_salary).sum();
        long   lowSalary  = all.stream().filter(e -> e.getEmp_salary() != null
                              && e.getEmp_salary() < LOW_SALARY_THRESHOLD).count();
        long   unresolved = hrAlertRepository.countByResolvedFalse();

        // Breakdown by city
        List<Map<String, Object>> cityBreakdown = all.stream()
            .filter(e -> e.getEmp_city() != null && e.getEmp_salary() != null)
            .collect(Collectors.groupingBy(Employee::getEmp_city,
                Collectors.summarizingDouble(Employee::getEmp_salary)))
            .entrySet().stream()
            .map(entry -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("city",          entry.getKey());
                m.put("employeeCount", entry.getValue().getCount());
                m.put("avgSalary",     Math.round(entry.getValue().getAverage()));
                m.put("totalPayroll",  Math.round(entry.getValue().getSum()));
                return m;
            })
            .sorted(Comparator.comparingLong(m -> -((Number) m.get("employeeCount")).longValue()))
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("MONTHLY_REPORT");
        r.setSummary(Map.of(
            "month",                 monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            "totalEmployees",        all.size(),
            "totalPayroll",          Math.round(payroll),
            "averageSalary",         Math.round(avg),
            "lowSalaryCount",        lowSalary,
            "salaryChangesThisMonth", monthChanges.size(),
            "unresolvedAlerts",      unresolved
        ));
        r.setDetails(cityBreakdown);
        return r;
    }

    @Override
    public AnalyticsReport generateWeeklyAnalytics() {
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);

        List<Employee> all          = employeeRepository.findAll();
        List<SalaryHistory> changes = salaryHistoryRepository.findByChangedAtBetween(weekStart, LocalDateTime.now());
        List<HrAlert> weekAlerts    = hrAlertRepository.findByCreatedAtAfterOrderByCreatedAtDesc(weekStart);

        double avg       = all.stream().filter(e -> e.getEmp_salary() != null)
                             .mapToDouble(Employee::getEmp_salary).average().orElse(0);
        long   lowSal    = all.stream().filter(e -> e.getEmp_salary() != null
                             && e.getEmp_salary() < LOW_SALARY_THRESHOLD).count();
        long   unresolved = hrAlertRepository.countByResolvedFalse();

        // Alerts by type
        List<Map<String, Object>> alertSummary = weekAlerts.stream()
            .collect(Collectors.groupingBy(HrAlert::getAlertType, Collectors.counting()))
            .entrySet().stream()
            .map(e -> Map.<String, Object>of("alertType", e.getKey(), "count", e.getValue()))
            .collect(Collectors.toList());

        AnalyticsReport r = new AnalyticsReport("WEEKLY_ANALYTICS");
        r.setSummary(Map.of(
            "weekStart",            weekStart.toString(),
            "totalEmployees",       all.size(),
            "averageSalary",        Math.round(avg),
            "lowSalaryEmployees",   lowSal,
            "salaryChanges",        changes.size(),
            "newAlertsThisWeek",    weekAlerts.size(),
            "unresolvedAlerts",     unresolved
        ));
        r.setDetails(alertSummary);
        return r;
    }

    // ============================================================ SYSTEM HEALTH

    @Override
    public AnalyticsReport getSystemHealth() {
        List<Employee> all = employeeRepository.findAll();

        long totalEmp      = all.size();
        long totalAssets   = assetRepo.count();
        long unresolved    = hrAlertRepository.countByResolvedFalse();
        long totalAlerts   = hrAlertRepository.count();
        long lowSal        = all.stream().filter(e -> e.getEmp_salary() != null
                                && e.getEmp_salary() < LOW_SALARY_THRESHOLD).count();
        long missingData   = all.stream().filter(this::hasMissingData).count();
        long dupGroups     = all.stream()
                                .filter(e -> e.getEmp_name() != null)
                                .collect(Collectors.groupingBy(
                                    e -> e.getEmp_name().trim().toLowerCase(),
                                    Collectors.counting()))
                                .values().stream().filter(c -> c > 1).count();

        String status;
        if (unresolved > 10 || (totalEmp > 0 && lowSal > totalEmp * 0.3)) {
            status = "CRITICAL";
        } else if (unresolved > 5 || (totalEmp > 0 && lowSal > totalEmp * 0.15)) {
            status = "WARNING";
        } else {
            status = "HEALTHY";
        }

        AnalyticsReport r = new AnalyticsReport("SYSTEM_HEALTH");
        r.setSummary(Map.of(
            "status",                   status,
            "totalEmployees",           totalEmp,
            "totalAssets",              totalAssets,
            "unresolvedAlerts",         unresolved,
            "totalAlertsEver",          totalAlerts,
            "lowSalaryEmployees",       lowSal,
            "incompleteRecords",        missingData,
            "duplicateNameGroups",      dupGroups
        ));
        r.setDetails(Collections.emptyList());
        return r;
    }

    // ============================================================ HIRING TREND

    @Override
    public AnalyticsReport getHiringTrend() {
        List<Employee> all = employeeRepository.findAll();

        Map<String, Long> byCity = all.stream()
            .filter(e -> e.getEmp_city() != null)
            .collect(Collectors.groupingBy(Employee::getEmp_city, Collectors.counting()));

        Map<String, Long> byAgeGroup = all.stream()
            .collect(Collectors.groupingBy(e -> {
                int age = e.getEmp_age();
                if (age < 25)  return "18-24";
                if (age < 35)  return "25-34";
                if (age < 45)  return "35-44";
                if (age < 55)  return "45-54";
                return "55+";
            }, Collectors.counting()));

        Map<String, Long> bySalaryBand = all.stream()
            .filter(e -> e.getEmp_salary() != null)
            .collect(Collectors.groupingBy(e -> {
                float s = e.getEmp_salary();
                if (s < 20_000)  return "Below 20k";
                if (s < 40_000)  return "20k-40k";
                if (s < 60_000)  return "40k-60k";
                if (s < 80_000)  return "60k-80k";
                return "80k+";
            }, Collectors.counting()));

        List<Map<String, Object>> details = new ArrayList<>();

        byCity.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("category", "city");
                m.put("label",    e.getKey());
                m.put("count",    e.getValue());
                details.add(m);
            });

        List.of("18-24","25-34","35-44","45-54","55+").forEach(group -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", "ageGroup");
            m.put("label",    group);
            m.put("count",    byAgeGroup.getOrDefault(group, 0L));
            details.add(m);
        });

        List.of("Below 20k","20k-40k","40k-60k","60k-80k","80k+").forEach(band -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("category", "salaryBand");
            m.put("label",    band);
            m.put("count",    bySalaryBand.getOrDefault(band, 0L));
            details.add(m);
        });

        double avgAge = all.stream().mapToInt(Employee::getEmp_age).average().orElse(0);

        AnalyticsReport r = new AnalyticsReport("HIRING_TREND");
        r.setSummary(Map.of(
            "totalEmployees",  all.size(),
            "citiesPresent",   byCity.size(),
            "avgEmployeeAge",  Math.round(avgAge * 10.0) / 10.0
        ));
        r.setDetails(details);
        return r;
    }

    // ============================================================ ALERTS

    @Override
    public List<HrAlert> getAllAlerts() {
        return hrAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<HrAlert> getUnresolvedAlerts() {
        return hrAlertRepository.findByResolvedFalseOrderByCreatedAtDesc();
    }

    @Override
    public HrAlert resolveAlert(Long alertId) {
        HrAlert alert = hrAlertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return hrAlertRepository.save(alert);
    }

    // ============================================================ MONITORING CHECKS

    @Override
    public void runAllChecks() {
        log.info("=== HR Monitoring: running all checks ===");
        runLowSalaryCheck();
        runAnomalyCheck();
        runSpikeCheck();
        runResignationRiskCheck();
        runDuplicateCheck();
        runMissingDataCheck();
        log.info("=== HR Monitoring: all checks completed ===");
    }

    private void runLowSalaryCheck() {
        employeeRepository.findAll().stream()
            .filter(e -> e.getEmp_salary() != null && e.getEmp_salary() < LOW_SALARY_THRESHOLD)
            .forEach(emp -> {
                if (!hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse(emp.getEmpCode(), "LOW_SALARY")) {
                    String sev = emp.getEmp_salary() < 10_000 ? "CRITICAL" : "HIGH";
                    HrAlert a = buildAlert("LOW_SALARY", sev, emp.getEmpCode(),
                        String.format("Employee %s (%s) salary %.0f is below threshold %.0f",
                            emp.getEmp_name(), emp.getEmpCode(), emp.getEmp_salary(), LOW_SALARY_THRESHOLD));
                    hrAlertRepository.save(a);
                    log.warn("[ALERT] LOW_SALARY — empCode={} salary={}", emp.getEmpCode(), emp.getEmp_salary());
                }
            });
    }

    private void runAnomalyCheck() {
        List<Employee> all = employeeRepository.findAll().stream()
            .filter(e -> e.getEmp_salary() != null).collect(Collectors.toList());
        if (all.size() < 3) return;

        double mean   = all.stream().mapToDouble(e -> e.getEmp_salary()).average().orElse(0);
        double stddev = Math.sqrt(all.stream()
            .mapToDouble(e -> Math.pow(e.getEmp_salary() - mean, 2)).average().orElse(0));
        if (stddev == 0) return;

        all.stream()
            .filter(e -> Math.abs(e.getEmp_salary() - mean) > ANOMALY_Z_SCORE * stddev)
            .forEach(emp -> {
                if (!hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse(emp.getEmpCode(), "SALARY_ANOMALY")) {
                    double z = (emp.getEmp_salary() - mean) / stddev;
                    HrAlert a = buildAlert("SALARY_ANOMALY", "MEDIUM", emp.getEmpCode(),
                        String.format("Salary anomaly for %s (%s): %.0f is %.1f σ from mean %.0f",
                            emp.getEmp_name(), emp.getEmpCode(), emp.getEmp_salary(), z, mean));
                    hrAlertRepository.save(a);
                    log.warn("[ALERT] SALARY_ANOMALY — empCode={} zScore={}", emp.getEmpCode(), Math.round(z * 100.0) / 100.0);
                }
            });
    }

    private void runSpikeCheck() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        salaryHistoryRepository.findRecentChanges(since).stream()
            .filter(sh -> sh.getOldSalary() != null && sh.getOldSalary() > 0 && sh.getNewSalary() != null)
            .filter(sh -> {
                double pct = Math.abs((sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary());
                return pct >= SPIKE_PCT_THRESHOLD;
            })
            .forEach(sh -> {
                String empCode = sh.getEmployee().getEmpCode();
                if (!hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse(empCode, "SALARY_SPIKE")) {
                    double pct = (sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary() * 100;
                    HrAlert a = buildAlert("SALARY_SPIKE", "HIGH", empCode,
                        String.format("Salary spike for %s (%s): %.0f → %.0f (%.1f%%) changed by %s",
                            sh.getEmployee().getEmp_name(), empCode,
                            sh.getOldSalary(), sh.getNewSalary(), pct, sh.getChangedBy()));
                    hrAlertRepository.save(a);
                    log.warn("[ALERT] SALARY_SPIKE — empCode={} change={}%", empCode, Math.round(pct));
                }
            });
    }

    private void runResignationRiskCheck() {
        List<Employee> all = employeeRepository.findAll();
        double avg = all.stream().filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).average().orElse(0);

        all.forEach(emp -> {
            int score = 0;
            if (emp.getEmp_salary() != null && emp.getEmp_salary() < LOW_SALARY_THRESHOLD) score += 40;
            if (emp.getEmp_salary() != null && avg > 0 && emp.getEmp_salary() < avg * 0.8)  score += 20;
            if (emp.getEmp_age() > 55) score += 10;

            if (score >= HIGH_RISK_SCORE
                    && !hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse(emp.getEmpCode(), "RESIGNATION_RISK")) {
                HrAlert a = buildAlert("RESIGNATION_RISK", "HIGH", emp.getEmpCode(),
                    String.format("HIGH resignation risk for %s (%s) — score: %d, salary: %.0f",
                        emp.getEmp_name(), emp.getEmpCode(), score, emp.getEmp_salary()));
                hrAlertRepository.save(a);
                log.warn("[ALERT] RESIGNATION_RISK — empCode={} score={}", emp.getEmpCode(), score);
            }
        });
    }

    private void runDuplicateCheck() {
        List<Employee> all = employeeRepository.findAll();
        all.stream()
            .filter(e -> e.getEmp_name() != null)
            .collect(Collectors.groupingBy(e -> e.getEmp_name().trim().toLowerCase()))
            .entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .forEach(entry -> {
                if (!hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse("SYSTEM", "DUPLICATE_RECORD")) {
                    HrAlert a = buildAlert("DUPLICATE_RECORD", "MEDIUM", "SYSTEM",
                        String.format("Duplicate name '%s' found %d times in employee records",
                            entry.getKey(), entry.getValue().size()));
                    hrAlertRepository.save(a);
                    log.warn("[ALERT] DUPLICATE_RECORD — name={} count={}", entry.getKey(), entry.getValue().size());
                }
            });
    }

    private void runMissingDataCheck() {
        employeeRepository.findAll().stream()
            .filter(this::hasMissingData)
            .forEach(emp -> {
                if (!hrAlertRepository.existsByEmpCodeAndAlertTypeAndResolvedFalse(emp.getEmpCode(), "MISSING_DATA")) {
                    HrAlert a = buildAlert("MISSING_DATA", "LOW", emp.getEmpCode(),
                        String.format("Missing fields for %s (%s): %s",
                            emp.getEmp_name(), emp.getEmpCode(),
                            String.join(", ", missingFields(emp))));
                    hrAlertRepository.save(a);
                    log.warn("[ALERT] MISSING_DATA — empCode={} fields={}", emp.getEmpCode(), missingFields(emp));
                }
            });
    }
}
