package com.example.EmployeeApiCrud.repository;

import com.example.EmployeeApiCrud.Model.ProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRegistrationRepo extends JpaRepository<ProjectRegistration,Long> {
}
