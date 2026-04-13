package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.BuilderRmDetails;
import com.example.EmployeeApiCrud.repository.BuilderRmDetailsRepo;
import com.example.EmployeeApiCrud.service.BuilderRmDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuilderRmDetailsServiceImpl implements BuilderRmDetailsService {

    @Autowired
    private BuilderRmDetailsRepo builderRmDetailsRepo;

    @Override
    public BuilderRmDetails create(BuilderRmDetails builderRmDetails) {
        return builderRmDetailsRepo.save(builderRmDetails);
    }

    @Override
    public BuilderRmDetails update(Long id, BuilderRmDetails builderRmDetails) {
        BuilderRmDetails existing = builderRmDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Builder RM not found with id: " + id));

        existing.setCompanyName(builderRmDetails.getCompanyName());
        existing.setFullName(builderRmDetails.getFullName());
        existing.setDesignation(builderRmDetails.getDesignation());
        existing.setMobileNo(builderRmDetails.getMobileNo());
        existing.setBirthdayDate(builderRmDetails.getBirthdayDate());
        existing.setSpouseFullName(builderRmDetails.getSpouseFullName());
        existing.setSpouseBirthdayDate(builderRmDetails.getSpouseBirthdayDate());
        existing.setMarriageAnniversaryDate(builderRmDetails.getMarriageAnniversaryDate());
        existing.setFirstChildFullName(builderRmDetails.getFirstChildFullName());
        existing.setFirstChildBirthdayDate(builderRmDetails.getFirstChildBirthdayDate());
        existing.setSecondChildFullName(builderRmDetails.getSecondChildFullName());
        existing.setSecondChildBirthdayDate(builderRmDetails.getSecondChildBirthdayDate());

        return builderRmDetailsRepo.save(existing);
    }

    @Override
    public BuilderRmDetails getById(Long id) {
        return builderRmDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Builder RM not found with id: " + id));
    }

    @Override
    public List<BuilderRmDetails> getAll() {
        return builderRmDetailsRepo.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!builderRmDetailsRepo.existsById(id))
            throw new RuntimeException("Builder RM not found with id: " + id);
        builderRmDetailsRepo.deleteById(id);
    }
}
