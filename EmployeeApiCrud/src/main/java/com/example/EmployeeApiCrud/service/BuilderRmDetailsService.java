package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.BuilderRmDetails;

import java.util.List;

public interface BuilderRmDetailsService {

    BuilderRmDetails create(BuilderRmDetails builderRmDetails);

    BuilderRmDetails update(Long id, BuilderRmDetails builderRmDetails);

    BuilderRmDetails getById(Long id);

    List<BuilderRmDetails> getAll();

    void delete(Long id);
}
