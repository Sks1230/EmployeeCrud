package com.example.EmployeeApiCrud.controller;

import com.example.EmployeeApiCrud.Model.MtrsDetails;
import com.example.EmployeeApiCrud.dto.APIResponse;
import com.example.EmployeeApiCrud.service.MtrsDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mtrs")
public class MtrsDetailsController {

    @Autowired
    private MtrsDetailsService mtrsDetailsService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> create(
            @RequestParam(required = false) String mtrsId,
            @RequestParam(required = false) String mainTeam,
            @RequestParam(required = false) String subTeam,
            @RequestParam(required = false) String builder,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String unitNo,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) MultipartFile uploadFile
    ) {
        try {
            MtrsDetails obj = build(mtrsId, mainTeam, subTeam, builder, project, client, unitNo, area);
            return ResponseEntity.ok(new APIResponse(1, "MTRS record created successfully",
                    mtrsDetailsService.create(obj, uploadFile)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<APIResponse> update(
            @PathVariable Long id,
            @RequestParam(required = false) String mtrsId,
            @RequestParam(required = false) String mainTeam,
            @RequestParam(required = false) String subTeam,
            @RequestParam(required = false) String builder,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String client,
            @RequestParam(required = false) String unitNo,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) MultipartFile uploadFile
    ) {
        try {
            MtrsDetails obj = build(mtrsId, mainTeam, subTeam, builder, project, client, unitNo, area);
            return ResponseEntity.ok(new APIResponse(1, "MTRS record updated successfully",
                    mtrsDetailsService.update(id, obj, uploadFile)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<APIResponse> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new APIResponse(1, "MTRS record fetched successfully",
                    mtrsDetailsService.getById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<APIResponse> getAll() {
        try {
            return ResponseEntity.ok(new APIResponse(1, "All MTRS records fetched successfully",
                    mtrsDetailsService.getAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<APIResponse> delete(@PathVariable Long id) {
        try {
            mtrsDetailsService.delete(id);
            return ResponseEntity.ok(new APIResponse(1, "MTRS record deleted successfully with ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(0, e.getMessage()));
        }
    }

    private MtrsDetails build(String mtrsId, String mainTeam, String subTeam, String builder,
                               String project, String client, String unitNo, String area) {
        MtrsDetails obj = new MtrsDetails();
        obj.setMtrsId(mtrsId);
        obj.setMainTeam(mainTeam);
        obj.setSubTeam(subTeam);
        obj.setBuilder(builder);
        obj.setProject(project);
        obj.setClient(client);
        obj.setUnitNo(unitNo);
        obj.setArea(area);
        return obj;
    }
}
