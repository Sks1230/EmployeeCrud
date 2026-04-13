package com.example.EmployeeApiCrud.mvc;

import com.example.EmployeeApiCrud.Model.Place;
import com.example.EmployeeApiCrud.service.PlaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/places")
public class PlaceMvcController {

    private final PlaceService placeService;

    public PlaceMvcController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "places");
        model.addAttribute("places", placeService.getAllPlaces());
        return "place/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("currentPage", "places");
        model.addAttribute("place", new Place());
        return "place/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("currentPage", "places");
            model.addAttribute("place", placeService.getPlaceById(id));
            return "place/form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/places";
        }
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) String place,
                       @RequestParam(required = false) String createdBy,
                       @RequestParam(required = false) MultipartFile file,
                       RedirectAttributes ra) {
        try {
            placeService.createPlace(place, createdBy, file);
            ra.addFlashAttribute("success", "Place created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/places";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam(required = false) String place,
                         @RequestParam(required = false) MultipartFile file,
                         RedirectAttributes ra) {
        try {
            placeService.updatePlace(id, place, file);
            ra.addFlashAttribute("success", "Place updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/places";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            placeService.deletePlace(id);
            ra.addFlashAttribute("success", "Place deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/places";
    }
}
