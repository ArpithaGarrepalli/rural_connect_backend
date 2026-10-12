package com.ruralconnect.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruralconnect.backend.entity.Crop;
import com.ruralconnect.backend.repository.CropRepository;

@Service
public class AdminService {
    private final CropRepository cropRepository;

    public AdminService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    @Transactional(readOnly = true)
    public List<Crop> getPendingCrops() {
        return cropRepository.findByApprovedFalse();
    }

    @Transactional
    public Crop approveCrop(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found"));
        if (crop.isApproved()) throw new IllegalArgumentException("Crop is already approved");
        crop.setApproved(true);
        return cropRepository.save(crop);
    }
}
