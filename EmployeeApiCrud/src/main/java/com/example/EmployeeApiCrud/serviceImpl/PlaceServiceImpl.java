package com.example.EmployeeApiCrud.serviceImpl;

import com.example.EmployeeApiCrud.Model.Place;
import com.example.EmployeeApiCrud.repository.PlaceRepository;
import com.example.EmployeeApiCrud.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PlaceServiceImpl implements PlaceService {

    private static final Logger log = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private final PlaceRepository placeRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public Place createPlace(String place, String createdBy, MultipartFile file) {
        try {
            Place entity = new Place();
            entity.setPlace(place);
            entity.setCreatedBy(createdBy);

            if (file != null && !file.isEmpty()) {
                entity.setAttachment(saveFile(file));
            }

            Place saved = placeRepository.save(entity);
            log.info("Place created with id={}", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Failed to create place: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Place updatePlace(Long id, String place, MultipartFile file) {
        try {
            Place entity = placeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Place not found with ID: " + id));

            entity.setPlace(place);

            if (file != null && !file.isEmpty()) {
                entity.setAttachment(saveFile(file));
            }

            Place saved = placeRepository.save(entity);
            log.info("Place updated with id={}", saved.getId());
            return saved;

        } catch (Exception e) {
            log.error("Failed to update place id={}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found with ID: " + id));
    }

    @Override
    public List<Place> getAllPlaces() {
        return placeRepository.findAll(Sort.by("createdDate").descending());
    }

    @Override
    public void deletePlace(Long id) {
        if (placeRepository.existsById(id)) {
            placeRepository.deleteById(id);
            log.info("Place deleted with id={}", id);
        } else {
            throw new RuntimeException("Place not found with ID: " + id);
        }
    }

    private String saveFile(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") &&
                !contentType.equals("image/png") &&
                !contentType.equals("application/pdf"))) {
            throw new RuntimeException("Only JPG, PNG, and PDF files are allowed");
        }

        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String fileName = System.currentTimeMillis() + "_" + originalName;

        Path path = Paths.get("uploads/places/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return "uploads/places/" + fileName;
    }
}
