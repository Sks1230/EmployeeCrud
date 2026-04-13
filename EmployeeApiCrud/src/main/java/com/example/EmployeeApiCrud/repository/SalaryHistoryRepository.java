package com.example.EmployeeApiCrud.repository;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.Model.SalaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, Long> {

    List<SalaryHistory> findByEmployee(Employee employee);

    List<SalaryHistory> findByEmployeeOrderByChangedAtDesc(Employee employee);

    List<SalaryHistory> findByChangedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.changedAt >= :since ORDER BY sh.changedAt DESC")
    List<SalaryHistory> findRecentChanges(@Param("since") LocalDateTime since);

    @Query("SELECT sh FROM SalaryHistory sh WHERE sh.employee.empCode = :empCode ORDER BY sh.changedAt DESC")
    List<SalaryHistory> findByEmpCode(@Param("empCode") String empCode);
}
