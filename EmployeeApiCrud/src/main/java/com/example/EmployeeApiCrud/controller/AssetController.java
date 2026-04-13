package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.dto.AssetRequestBody;
import com.example.EmployeeApiCrud.service.AssetService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;
    private final ObjectMapper objectMapper;

    public AssetController(AssetService assetService, ObjectMapper objectMapper) {
        this.assetService = assetService;
        this.objectMapper = objectMapper;
    }

    // CREATE ASSET WITH FILE UPLOAD
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> createAsset(
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        try {

            AssetRequestBody assetRequestBody = objectMapper.readValue(data, AssetRequestBody.class);

            return ResponseEntity.ok(
                    new APIResponse(
                            1,
                            "Asset created successfully",
                            assetService.createAsset(assetRequestBody, file)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }

    // UPDATE ASSET
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse> updateAsset(
            @PathVariable("id") Long id,
            @RequestBody AssetRequestBody assetRequestBody
    ) {

        try {

            return ResponseEntity.ok(
                    new APIResponse(
                            1,
                            "Asset updated successfully",
                            assetService.updateAsset(id, assetRequestBody)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }


    // GET ASSET BY ID
    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getAssetById(
            @PathVariable("id") Long id
    ) {

        try {

            return ResponseEntity.ok(
                    new APIResponse(
                            1,
                            "Asset fetched successfully",
                            assetService.getAssetById(id)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }


    // GET ALL ASSETS
    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAllAsset() {

        try {

            return ResponseEntity.ok(
                    new APIResponse(
                            1,
                            "All asset fetched successfully",
                            assetService.getAllAsset()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }


    // GET ASSET HISTORY
    @GetMapping("/history/{id}")
    public ResponseEntity<APIResponse> getAssetHistory(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    new APIResponse(
                            1,
                            "Asset history fetched successfully",
                            assetService.getAllAssetHistory(id)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }


    // DELETE ASSET
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deleteAsset(
            @PathVariable Long id
    ) {

        try {

            assetService.deleteAsset(id);

            return ResponseEntity.ok(
                    new APIResponse(1, "Asset deleted successfully")
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new APIResponse(0, e.getMessage()));
        }
    }

}