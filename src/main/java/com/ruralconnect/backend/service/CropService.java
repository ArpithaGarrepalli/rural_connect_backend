
package com.ruralconnect.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruralconnect.backend.entity.Crop;
import com.ruralconnect.backend.entity.Role;
import com.ruralconnect.backend.entity.User;
import com.ruralconnect.backend.repository.CropRepository;
import com.ruralconnect.backend.repository.UserRepository;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    public CropService(
            CropRepository cropRepository,
            UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Crop addCrop(
            Long farmerId,
            String name,
            String category,
            BigDecimal quantityKg,
            BigDecimal pricePerKg,
            BigDecimal wholesalePricePerKg,
            BigDecimal discount25Kg,
            BigDecimal discount50Kg,
            BigDecimal discount100Kg,
            String description) {

        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() ->
                        new RuntimeException("Farmer account not found"));

        if (farmer.getRole() != Role.FARMER) {
            throw new RuntimeException(
                    "Only farmer accounts can add crops");
        }

        if (!farmer.isActive()) {
            throw new RuntimeException("This account is inactive");
        }

        if (name == null || name.isBlank()
                || category == null || category.isBlank()) {
            throw new RuntimeException(
                    "Crop name and category are required");
        }

        if (quantityKg == null
                || quantityKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        if (pricePerKg == null
                || pricePerKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Price per kg must be greater than zero");
        }

        Crop crop = new Crop();
        crop.setName(name.trim());
        crop.setCategory(category.trim());
        crop.setQuantityKg(quantityKg);
        crop.setPricePerKg(pricePerKg);
        crop.setWholesalePricePerKg(wholesalePricePerKg);
        crop.setDiscount25Kg(discount25Kg);
        crop.setDiscount50Kg(discount50Kg);
        crop.setDiscount100Kg(discount100Kg);
        crop.setDescription(description);
        crop.setFarmer(farmer);

        // New crops require admin approval before being listed.
        crop.setApproved(false);

        return cropRepository.save(crop);
    }

    @Transactional(readOnly = true)
    public List<Crop> getApprovedCrops() {
        return cropRepository.findByApprovedTrue();
    }

    @Transactional(readOnly = true)
    public List<Crop> getFarmerCrops(Long farmerId) {
        return cropRepository.findByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public List<Crop> searchApprovedCrops(String name) {
        if (name == null || name.isBlank()) {
            return getApprovedCrops();
        }

        return cropRepository
                .findByNameContainingIgnoreCaseAndApprovedTrue(
                        name.trim());
    }
}