package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    // CREATE PLACE
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> createPlace(
            @RequestParam(value = "place", required = false) String place,
            @RequestParam(value = "createdBy", required = false) String createdBy,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            return ResponseEntity.ok(
                    new APIResponse(1, "Place created successfully", placeService.createPlace(place, createdBy, file))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    // UPDATE PLACE
    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> updatePlace(
            @PathVariable Long id,
            @RequestParam(value = "place", required = false) String place,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            return ResponseEntity.ok(
                    new APIResponse(1, "Place updated successfully", placeService.updatePlace(id, place, file))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    // GET PLACE BY ID
    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getPlaceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new APIResponse(1, "Place fetched successfully", placeService.getPlaceById(id))
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    // GET ALL PLACES
    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAllPlaces() {
        try {
            return ResponseEntity.ok(
                    new APIResponse(1, "All places fetched successfully", placeService.getAllPlaces())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    // DELETE PLACE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> deletePlace(@PathVariable Long id) {
        try {
            placeService.deletePlace(id);
            return ResponseEntity.ok(new APIResponse(1, "Place deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }
}
