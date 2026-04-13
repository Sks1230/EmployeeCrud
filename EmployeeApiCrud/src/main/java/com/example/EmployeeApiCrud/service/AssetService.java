package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.Asset;
import com.example.EmployeeApiCrud.Model.AssetHistory;
import com.example.EmployeeApiCrud.dto.AssetRequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssetService {

    Asset createAsset(AssetRequestBody assetRequestBody, MultipartFile file);

    Asset updateAsset(Long id, AssetRequestBody assetRequestBody);

    Asset getAssetById(Long id);

    List<Asset> getAllAsset();

    void deleteAsset(Long id);

    List<AssetHistory> getAllAssetHistory(Long id);
}
