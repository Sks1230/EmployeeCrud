package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.Fnf;

import java.util.List;

public interface FnfService {

    Fnf create(Fnf fnf);

    Fnf update(Long id, Fnf fnf);

    Fnf getById(Long id);

    List<Fnf> getAll();

    void delete(Long id);
}
