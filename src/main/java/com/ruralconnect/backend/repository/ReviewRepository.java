package com.ruralconnect.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ruralconnect.backend.entity.Review;
import com.ruralconnect.backend.entity.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStatus(ReviewStatus status);
    List<Review> findByCropIdAndStatus(Long cropId, ReviewStatus status);
    List<Review> findByCropFarmerId(Long farmerId);
    boolean existsByOrderId(Long orderId);
}
