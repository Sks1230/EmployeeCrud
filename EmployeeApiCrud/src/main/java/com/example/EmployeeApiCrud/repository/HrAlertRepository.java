package com.example.EmployeeApiCrud.repository;

import com.example.EmployeeApiCrud.Model.HrAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HrAlertRepository extends JpaRepository<HrAlert, Long> {

    List<HrAlert> findByResolvedFalseOrderByCreatedAtDesc();

    List<HrAlert> findAllByOrderByCreatedAtDesc();

    List<HrAlert> findByAlertType(String alertType);

    List<HrAlert> findByEmpCode(String empCode);

    List<HrAlert> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime after);

    List<HrAlert> findBySeverity(String severity);

    boolean existsByEmpCodeAndAlertTypeAndResolvedFalse(String empCode, String alertType);

    long countByResolvedFalse();
}
