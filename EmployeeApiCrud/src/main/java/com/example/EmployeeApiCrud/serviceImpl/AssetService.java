package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Asset;
import com.example.EmployeeApiCrud.Model.AssetHistory;
import com.example.EmployeeApiCrud.dto.AssetHistoryResponseBody;
import com.example.EmployeeApiCrud.dto.AssetRequestBody;
import com.example.EmployeeApiCrud.repository.AssetRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AssetService implements com.example.EmployeeApiCrud.service.AssetService {

    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepo assetRepo;
    private final ObjectMapper objectMapper;

    public AssetService(AssetRepo assetRepo, ObjectMapper objectMapper) {
        this.assetRepo = assetRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public Asset createAsset(AssetRequestBody assetRequestBody, MultipartFile file) {

        try {

            Asset asset = new Asset();

            asset.setType(assetRequestBody.getType());
            asset.setIsActive(assetRequestBody.getIsActive());
            asset.setEmp_code(assetRequestBody.getEmp_code());
            asset.setAssetStatus(assetRequestBody.getAssetStatus());
            asset.setRemarks(assetRequestBody.getRemarks());
            asset.setDepartment(assetRequestBody.getDepartment());
            asset.setSerialNumber(assetRequestBody.getSerialNumber());
            asset.setBranch(assetRequestBody.getBranch());

            if (assetRequestBody.getEmp_code() != null) {
                asset.setAssignedDate(new Date());
            }

            if (file != null && !file.isEmpty()) {
                asset.setAssetFile(saveFile(file, "uploads/assets/"));
            }

            Asset saved = assetRepo.save(asset);
            log.info("Asset created with id={}", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Failed to create asset: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Asset updateAsset(Long id, AssetRequestBody assetRequestBody) {

        try {

            Asset asset = assetRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invalid Asset Id!"));

            boolean isUpdated = isUpdated(asset, assetRequestBody);

            if (isUpdated) {
                List<AssetHistory> history = asset.getHistory();
                history.add(new AssetHistory(
                        asset,
                        objectMapper.writeValueAsString(
                                new AssetHistoryResponseBody(asset)
                        )
                ));
                asset.setHistory(history);
            }

            asset.setType(assetRequestBody.getType());
            asset.setIsActive(assetRequestBody.getIsActive());
            asset.setAssetStatus(assetRequestBody.getAssetStatus());
            asset.setRemarks(assetRequestBody.getRemarks());
            asset.setDepartment(assetRequestBody.getDepartment());
            asset.setBranch(assetRequestBody.getBranch());
            asset.setSerialNumber(assetRequestBody.getSerialNumber());

            if (assetRequestBody.getEmp_code() != null && !assetRequestBody.getEmp_code().equals(asset.getEmp_code())) {
                asset.setAssignedDate(new Date());
            }
            asset.setEmp_code(assetRequestBody.getEmp_code());

            Asset saved = assetRepo.save(asset);
            log.info("Asset updated with id={}", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Failed to update asset id={}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static boolean isUpdated(Asset asset, AssetRequestBody assetRequestBody) {

        if (!Objects.equals(asset.getType(), assetRequestBody.getType())) return true;
        else if (!Objects.equals(asset.getBranch(), assetRequestBody.getBranch())) return true;
        else if (!Objects.equals(asset.getRemarks(), assetRequestBody.getRemarks())) return true;
        else if (!Objects.equals(asset.getEmp_code(), assetRequestBody.getEmp_code())) return true;
        else if (!Objects.equals(asset.getIsActive(), assetRequestBody.getIsActive())) return true;
        else if (!Objects.equals(asset.getSerialNumber(), assetRequestBody.getSerialNumber())) return true;
        else return !Objects.equals(asset.getAssetStatus(), assetRequestBody.getAssetStatus());
    }

    @Override
    public Asset getAssetById(Long id) {
        return assetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with id"));
    }

    @Override
    public List<Asset> getAllAsset() {
        return assetRepo.findAll(Sort.by("createdDate").descending());
    }

    @Override
    public void deleteAsset(Long id) {
        if (assetRepo.existsById(id)) {
            assetRepo.deleteById(id);
            log.info("Asset deleted with id={}", id);
        } else {
            throw new RuntimeException("Asset not found with this ID");
        }
    }

    @Override
    public List<AssetHistory> getAllAssetHistory(Long id) {
        Asset asset = assetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid Asset Id!"));
        return asset.getHistory();
    }

    private String saveFile(MultipartFile file, String folder) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("application/pdf"))) {
            throw new RuntimeException("Only JPG, PNG, and PDF files are allowed");
        }

        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String fileName = System.currentTimeMillis() + "_" + originalName;

        Path path = Paths.get(folder + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return folder + fileName;
    }
}
