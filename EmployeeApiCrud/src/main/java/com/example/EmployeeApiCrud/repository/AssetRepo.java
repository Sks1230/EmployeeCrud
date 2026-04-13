package com.example.EmployeeApiCrud.repository;
//import com.moneytree.entities.Asset;
import com.example.EmployeeApiCrud.Model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepo extends JpaRepository<Asset,Long> {
}