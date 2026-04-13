package com.example.EmployeeApiCrud.service;

import com.example.EmployeeApiCrud.Model.Place;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PlaceService {

    Place createPlace(String place, String createdBy, MultipartFile file);

    Place updatePlace(Long id, String place, MultipartFile file);

    Place getPlaceById(Long id);

    List<Place> getAllPlaces();

    void deletePlace(Long id);
}
