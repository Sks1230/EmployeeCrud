package com.example.EmployeeApiCrud.repository;

import com.example.EmployeeApiCrud.Model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}
