package com.example.EmployeeApiCrud.repository;

import com.example.EmployeeApiCrud.Model.MtrsDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MtrsDetailsRepo extends JpaRepository<MtrsDetails, Long> {
}
