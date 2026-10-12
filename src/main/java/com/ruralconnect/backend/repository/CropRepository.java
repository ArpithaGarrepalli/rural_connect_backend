
package com.ruralconnect.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ruralconnect.backend.entity.Crop;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByApprovedTrue();

    List<Crop> findByApprovedFalse();

    List<Crop> findByFarmerId(Long farmerId);

    List<Crop> findByNameContainingIgnoreCaseAndApprovedTrue(String name);
}