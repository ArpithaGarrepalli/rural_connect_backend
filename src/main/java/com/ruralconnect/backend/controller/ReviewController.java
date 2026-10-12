package com.ruralconnect.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ruralconnect.backend.dto.ReviewResponse;
import com.ruralconnect.backend.entity.Review;
import com.ruralconnect.backend.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestParam Long customerId,
                                          @RequestParam Long orderId,
                                          @RequestBody Map<String, Object> body) {
        try {
            Object ratingValue = body.get("rating");
            Integer rating = ratingValue instanceof Number ? ((Number) ratingValue).intValue() : null;
            String comment = body.get("comment") == null ? "" : body.get("comment").toString();
            Review review = reviewService.submitReview(customerId, orderId, rating, comment);
            return ResponseEntity.ok(Map.of("message", "Review submitted and awaiting admin approval",
                    "review", new ReviewResponse(review)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/crop/{cropId}")
    public List<ReviewResponse> getApprovedCropReviews(@PathVariable Long cropId) {
        return reviewService.getApprovedReviewsForCrop(cropId).stream()
                .map(ReviewResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/farmer/{farmerId}")
    public List<ReviewResponse> getFarmerReviews(@PathVariable Long farmerId) {
        return reviewService.getFarmerReviews(farmerId).stream()
                .map(ReviewResponse::new).collect(Collectors.toList());
    }
}
