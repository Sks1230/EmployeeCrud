package com.example.EmployeeApiCrud.mvc;

import com.example.EmployeeApiCrud.Model.Asset;
import com.example.EmployeeApiCrud.dto.AssetRequestBody;
import com.example.EmployeeApiCrud.service.AssetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/assets")
public class AssetMvcController {

    private final AssetService assetService;

    public AssetMvcController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "assets");
        model.addAttribute("assets", assetService.getAllAsset());
        return "asset/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("currentPage", "assets");
        model.addAttribute("asset", new Asset());
        return "asset/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("currentPage", "assets");
            model.addAttribute("asset", assetService.getAssetById(id));
            return "asset/form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/assets";
        }
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) String type,
                       @RequestParam(required = false) String isActive,
                       @RequestParam(required = false) String emp_code,
                       @RequestParam(required = false) String assetStatus,
                       @RequestParam(required = false) String serialNumber,
                       @RequestParam(required = false) String department,
                       @RequestParam(required = false) String branch,
                       @RequestParam(required = false) String remarks,
                       @RequestParam(required = false) MultipartFile file,
                       RedirectAttributes ra) {
        try {
            AssetRequestBody req = buildRequest(type, isActive, emp_code, assetStatus, serialNumber, department, branch, remarks);
            assetService.createAsset(req, file);
            ra.addFlashAttribute("success", "Asset created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/assets";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam(required = false) String type,
                         @RequestParam(required = false) String isActive,
                         @RequestParam(required = false) String emp_code,
                         @RequestParam(required = false) String assetStatus,
                         @RequestParam(required = false) String serialNumber,
                         @RequestParam(required = false) String department,
                         @RequestParam(required = false) String branch,
                         @RequestParam(required = false) String remarks,
                         RedirectAttributes ra) {
        try {
            AssetRequestBody req = buildRequest(type, isActive, emp_code, assetStatus, serialNumber, department, branch, remarks);
            assetService.updateAsset(id, req);
            ra.addFlashAttribute("success", "Asset updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/assets";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            assetService.deleteAsset(id);
            ra.addFlashAttribute("success", "Asset deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/assets";
    }

    private AssetRequestBody buildRequest(String type, String isActive, String emp_code, String assetStatus,
                                          String serialNumber, String department, String branch, String remarks) {
        AssetRequestBody req = new AssetRequestBody();
        req.setType(type);
        req.setIsActive(isActive);
        req.setEmp_code(emp_code);
        req.setAssetStatus(assetStatus);
        req.setSerialNumber(serialNumber);
        req.setDepartment(department);
        req.setBranch(branch);
        req.setRemarks(remarks);
        return req;
    }
}
