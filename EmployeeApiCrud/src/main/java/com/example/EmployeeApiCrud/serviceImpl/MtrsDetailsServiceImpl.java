package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.MtrsDetails;
import com.example.EmployeeApiCrud.repository.MtrsDetailsRepo;
import com.example.EmployeeApiCrud.service.MtrsDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MtrsDetailsServiceImpl implements MtrsDetailsService {

    @Autowired
    private MtrsDetailsRepo mtrsDetailsRepo;

    @Override
    public MtrsDetails create(MtrsDetails mtrsDetails, MultipartFile uploadFile) {
        if (uploadFile != null && !uploadFile.isEmpty())
            mtrsDetails.setUploadFile(saveFile(uploadFile));
        return mtrsDetailsRepo.save(mtrsDetails);
    }

    @Override
    public MtrsDetails update(Long id, MtrsDetails mtrsDetails, MultipartFile uploadFile) {
        MtrsDetails existing = mtrsDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MTRS record not found with id: " + id));

        existing.setMtrsId(mtrsDetails.getMtrsId());
        existing.setMainTeam(mtrsDetails.getMainTeam());
        existing.setSubTeam(mtrsDetails.getSubTeam());
        existing.setBuilder(mtrsDetails.getBuilder());
        existing.setProject(mtrsDetails.getProject());
        existing.setClient(mtrsDetails.getClient());
        existing.setUnitNo(mtrsDetails.getUnitNo());
        existing.setArea(mtrsDetails.getArea());

        if (uploadFile != null && !uploadFile.isEmpty())
            existing.setUploadFile(saveFile(uploadFile));

        return mtrsDetailsRepo.save(existing);
    }

    @Override
    public MtrsDetails getById(Long id) {
        return mtrsDetailsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("MTRS record not found with id: " + id));
    }

    @Override
    public List<MtrsDetails> getAll() {
        return mtrsDetailsRepo.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!mtrsDetailsRepo.existsById(id))
            throw new RuntimeException("MTRS record not found with id: " + id);
        mtrsDetailsRepo.deleteById(id);
    }

    private String saveFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") &&
                    !contentType.equals("image/png") &&
                    !contentType.equals("application/pdf") &&
                    !contentType.equals("application/vnd.ms-powerpoint") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))) {
                throw new RuntimeException("Only JPG, PNG, PDF, and PPT files are allowed");
            }

            String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileName = System.currentTimeMillis() + "_" + originalName;
            Path path = Paths.get("uploads/mtrs/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return "uploads/mtrs/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}
