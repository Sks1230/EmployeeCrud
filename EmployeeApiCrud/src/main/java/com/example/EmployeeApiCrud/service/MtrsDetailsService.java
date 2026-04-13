package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.MtrsDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MtrsDetailsService {

    MtrsDetails create(MtrsDetails mtrsDetails, MultipartFile uploadFile);

    MtrsDetails update(Long id, MtrsDetails mtrsDetails, MultipartFile uploadFile);

    MtrsDetails getById(Long id);

    List<MtrsDetails> getAll();

    void delete(Long id);
}
