package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Fnf;
import com.example.EmployeeApiCrud.repository.FnfRepo;
import com.example.EmployeeApiCrud.service.FnfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FnfServiceImpl implements FnfService {

    @Autowired
    private FnfRepo fnfRepo;

    @Override
    public Fnf create(Fnf fnf) {
        return fnfRepo.save(fnf);
    }

    @Override
    public Fnf update(Long id, Fnf fnf) {
        Fnf existing = fnfRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("FNF record not found with id: " + id));
        existing.setCode(fnf.getCode());
        existing.setName(fnf.getName());
        existing.setHodName(fnf.getHodName());
        existing.setPercentage(fnf.getPercentage());
        existing.setRemarks(fnf.getRemarks());
        return fnfRepo.save(existing);
    }

    @Override
    public Fnf getById(Long id) {
        return fnfRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("FNF record not found with id: " + id));
    }

    @Override
    public List<Fnf> getAll() {
        return fnfRepo.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!fnfRepo.existsById(id))
            throw new RuntimeException("FNF record not found with id: " + id);
        fnfRepo.deleteById(id);
    }
}
