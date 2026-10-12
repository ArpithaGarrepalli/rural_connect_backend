
package com.ruralconnect.backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruralconnect.backend.entity.Crop;
import com.ruralconnect.backend.service.CropService;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "http://localhost:5173")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    // Add a crop
    @PostMapping
    public ResponseEntity<?> addCrop(
            @RequestParam Long farmerId,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam BigDecimal quantityKg,
            @RequestParam BigDecimal pricePerKg,
            @RequestParam(required = false) BigDecimal wholesalePricePerKg,
            @RequestParam(required = false) BigDecimal discount25Kg,
            @RequestParam(required = false) BigDecimal discount50Kg,
            @RequestParam(required = false) BigDecimal discount100Kg,
            @RequestParam(required = false) String description) {

        try {
            Crop crop = cropService.addCrop(
                    farmerId,
                    name,
                    category,
                    quantityKg,
                    pricePerKg,
                    wholesalePricePerKg,
                    discount25Kg,
                    discount50Kg,
                    discount100Kg,
                    description
            );

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Crop submitted for admin approval");
            result.put("id", crop.getId());
            result.put("name", crop.getName());
            result.put("category", crop.getCategory());
            result.put("quantityKg", crop.getQuantityKg());
            result.put("pricePerKg", crop.getPricePerKg());
            result.put("approved", crop.isApproved());

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // List approved crops for customers
    @GetMapping
    public List<Map<String, Object>> getApprovedCrops() {
        return cropService.getApprovedCrops()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Search approved crops by name
    @GetMapping("/search")
    public List<Map<String, Object>> searchCrops(
            @RequestParam String name) {

        return cropService.searchApprovedCrops(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // List crops belonging to a farmer
    @GetMapping("/farmer/{farmerId}")
    public List<Map<String, Object>> getFarmerCrops(
            @PathVariable Long farmerId) {

        return cropService.getFarmerCrops(farmerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Map<String, Object> toResponse(Crop crop) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", crop.getId());
        result.put("name", crop.getName());
        result.put("category", crop.getCategory());
        result.put("quantityKg", crop.getQuantityKg());
        result.put("pricePerKg", crop.getPricePerKg());
        result.put("wholesalePricePerKg", crop.getWholesalePricePerKg());
        result.put("discount25Kg", crop.getDiscount25Kg());
        result.put("discount50Kg", crop.getDiscount50Kg());
        result.put("discount100Kg", crop.getDiscount100Kg());
        result.put("description", crop.getDescription());
        result.put("approved", crop.isApproved());

        result.put("farmerId", crop.getFarmer().getId());
        result.put("farmerName", crop.getFarmer().getName());

        return result;
    }
}