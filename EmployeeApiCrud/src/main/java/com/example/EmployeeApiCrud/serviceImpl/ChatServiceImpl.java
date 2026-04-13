package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.Model.HrAlert;
import com.example.EmployeeApiCrud.Model.SalaryHistory;
import com.example.EmployeeApiCrud.dto.ChatResponse;
import com.example.EmployeeApiCrud.repository.EmployeeRepository;
import com.example.EmployeeApiCrud.repository.HrAlertRepository;
import com.example.EmployeeApiCrud.repository.SalaryHistoryRepository;
import com.example.EmployeeApiCrud.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:llama3.2}")
    private String ollamaModel;

    @Autowired private EmployeeRepository      employeeRepository;
    @Autowired private HrAlertRepository       hrAlertRepository;
    @Autowired private SalaryHistoryRepository salaryHistoryRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // ----------------------------------------------------------------

    @Override
    public ChatResponse ask(String question) {
        log.info("Chat question received: {}", question);

        // 1. Build context from DB
        String context = buildDatabaseContext(question);

        // 2. Build prompt
        String prompt = buildPrompt(context, question);

        // 3. Call Ollama
        String answer = callOllama(prompt);

        log.info("Chat answer generated for question: {}", question);
        return new ChatResponse(question, answer, ollamaModel);
    }

    // ---------------------------------------------------------------- DB Context Builder

    private String buildDatabaseContext(String question) {
        String q = question.toLowerCase();
        StringBuilder ctx = new StringBuilder();
        ctx.append("=== HR DATABASE ===\n\n");

        List<Employee> allEmployees = employeeRepository.findAll();

        // Always include summary stats
        ctx.append(buildSummaryStats(allEmployees));

        // Include full employee table always (important for most questions)
        ctx.append(buildEmployeeTable(allEmployees));

        // Salary history — if question is about salary change/growth/trend
        if (containsAny(q, "salary", "raise", "growth", "change", "badhaya", "trend", "history",
                "spike", "increase", "decrease", "badha", "ghataya", "payment", "payroll")) {
            ctx.append(buildRecentSalaryHistory());
        }

        // Alerts — if question is about alerts/risks/issues
        if (containsAny(q, "alert", "risk", "issue", "problem", "danger", "resign",
                "duplicate", "missing", "anomaly", "khatarnak", "problem")) {
            ctx.append(buildActiveAlerts());
        }

        return ctx.toString();
    }

    private String buildSummaryStats(List<Employee> employees) {
        if (employees.isEmpty()) return "No employees in database.\n\n";

        double avg = employees.stream()
            .filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).average().orElse(0);

        double max = employees.stream()
            .filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).max().orElse(0);

        double min = employees.stream()
            .filter(e -> e.getEmp_salary() != null)
            .mapToDouble(Employee::getEmp_salary).min().orElse(0);

        long lowSalary = employees.stream()
            .filter(e -> e.getEmp_salary() != null && e.getEmp_salary() < 20000).count();

        long unresolvedAlerts = hrAlertRepository.countByResolvedFalse();

        return String.format(
            "--- SUMMARY STATISTICS ---\n" +
            "Total Employees   : %d\n" +
            "Average Salary    : %.0f\n" +
            "Highest Salary    : %.0f\n" +
            "Lowest Salary     : %.0f\n" +
            "Below 20k Salary  : %d employees\n" +
            "Unresolved Alerts : %d\n\n",
            employees.size(), avg, max, min, lowSalary, unresolvedAlerts
        );
    }

    private String buildEmployeeTable(List<Employee> employees) {
        if (employees.isEmpty()) return "";

        StringBuilder sb = new StringBuilder("--- EMPLOYEE LIST ---\n");
        sb.append(String.format("%-6s %-6s %-20s %-10s %-5s %-15s\n",
            "ID", "Code", "Name", "Salary", "Age", "City"));
        sb.append("-".repeat(65)).append("\n");

        employees.forEach(e -> sb.append(String.format("%-6s %-6s %-20s %-10s %-5s %-15s\n",
            e.getEmpid(),
            e.getEmpCode()    != null ? e.getEmpCode()    : "N/A",
            e.getEmp_name()   != null ? e.getEmp_name()   : "N/A",
            e.getEmp_salary() != null ? String.format("%.0f", e.getEmp_salary()) : "N/A",
            e.getEmp_age(),
            e.getEmp_city()   != null ? e.getEmp_city()   : "N/A"
        )));

        sb.append("\n");
        return sb.toString();
    }

    private String buildRecentSalaryHistory() {
        LocalDateTime since = LocalDateTime.now().minusDays(90);
        List<SalaryHistory> history = salaryHistoryRepository.findRecentChanges(since);

        if (history.isEmpty()) return "--- SALARY HISTORY (last 90 days) ---\nNo salary changes recorded.\n\n";

        StringBuilder sb = new StringBuilder("--- SALARY HISTORY (last 90 days) ---\n");
        history.stream().limit(20).forEach(sh -> {
            double pct = 0;
            if (sh.getOldSalary() != null && sh.getOldSalary() > 0 && sh.getNewSalary() != null) {
                pct = (sh.getNewSalary() - sh.getOldSalary()) / sh.getOldSalary() * 100;
            }
            sb.append(String.format("  %s (%s): %.0f → %.0f  (%+.1f%%)  on %s  by %s\n",
                sh.getEmployee().getEmp_name(),
                sh.getEmployee().getEmpCode(),
                sh.getOldSalary() != null ? sh.getOldSalary() : 0f,
                sh.getNewSalary() != null ? sh.getNewSalary() : 0f,
                pct,
                sh.getChangedAt() != null
                    ? sh.getChangedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : "unknown",
                sh.getChangedBy() != null ? sh.getChangedBy() : "unknown"
            ));
        });

        sb.append("\n");
        return sb.toString();
    }

    private String buildActiveAlerts() {
        List<HrAlert> alerts = hrAlertRepository.findByResolvedFalseOrderByCreatedAtDesc();
        if (alerts.isEmpty()) return "--- ACTIVE HR ALERTS ---\nNo active alerts.\n\n";

        StringBuilder sb = new StringBuilder("--- ACTIVE HR ALERTS ---\n");
        alerts.stream().limit(15).forEach(a -> sb.append(String.format(
            "  [%s] %s — %s\n", a.getSeverity(), a.getAlertType(), a.getMessage()
        )));

        sb.append("\n");
        return sb.toString();
    }

    // ---------------------------------------------------------------- Prompt Builder

    private String buildPrompt(String context, String question) {
        return "You are an intelligent HR assistant. You have access to the company's live employee database shown below.\n" +
               "Answer the user's question ONLY based on this data. Be concise and helpful.\n" +
               "If the answer involves a list, format it clearly.\n" +
               "If the data doesn't contain the answer, say: 'Database mein yeh information available nahi hai.'\n" +
               "Reply in the same language the question is asked (Hindi or English).\n\n" +
               context +
               "=== USER QUESTION ===\n" +
               question + "\n\n" +
               "=== YOUR ANSWER ===\n";
    }

    // ---------------------------------------------------------------- Ollama API Call

    @SuppressWarnings("unchecked")
    private String callOllama(String prompt) {
        String url = ollamaBaseUrl + "/api/generate";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model",  ollamaModel);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            if (response != null && response.containsKey("response")) {
                return response.get("response").toString().trim();
            }
            return "Ollama se response nahi mila.";
        } catch (Exception ex) {
            log.error("Ollama call failed: {}", ex.getMessage());
            return "Ollama se connect nahi ho pa raha. Kripya check karein ki Ollama chal raha hai (ollama serve).\n" +
                   "Error: " + ex.getMessage();
        }
    }

    // ---------------------------------------------------------------- Helper

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }
}
