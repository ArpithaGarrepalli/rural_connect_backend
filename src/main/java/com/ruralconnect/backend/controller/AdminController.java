package com.ruralconnect.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ruralconnect.backend.dto.OrderResponse;
import com.ruralconnect.backend.dto.ReviewResponse;
import com.ruralconnect.backend.entity.Crop;
import com.ruralconnect.backend.entity.Order;
import com.ruralconnect.backend.entity.Review;
import com.ruralconnect.backend.service.AdminService;
import com.ruralconnect.backend.service.OrderService;
import com.ruralconnect.backend.service.ReviewService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminService adminService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public AdminController(AdminService adminService, OrderService orderService,
                           ReviewService reviewService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    @GetMapping("/crops/pending")
    public List<Map<String, Object>> getPendingCrops() {
        return adminService.getPendingCrops().stream().map(this::cropMap).collect(Collectors.toList());
    }

    @PutMapping("/crops/{cropId}/approve")
    public ResponseEntity<?> approveCrop(@PathVariable Long cropId) {
        try {
            return ResponseEntity.ok(cropMap(adminService.approveCrop(cropId)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/cancellations/pending")
    public List<OrderResponse> getPendingCancellations() {
        return orderService.getPendingCancellationRequests().stream()
                .map(OrderResponse::new).collect(Collectors.toList());
    }

    @PutMapping("/orders/{orderId}/cancellation-decision")
    public ResponseEntity<?> decideCancellation(@PathVariable Long orderId,
                                                 @RequestParam boolean approve,
                                                 @RequestBody(required = false) Map<String, String> body) {
        try {
            String note = body == null ? "" : body.get("note");
            Order order = orderService.decideCancellation(orderId, approve, note);
            return ResponseEntity.ok(Map.of("message",
                    approve ? "Cancellation approved" : "Cancellation rejected",
                    "order", new OrderResponse(order)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/reviews/pending")
    public List<ReviewResponse> getPendingReviews() {
        return reviewService.getPendingReviews().stream()
                .map(ReviewResponse::new).collect(Collectors.toList());
    }

    @PutMapping("/reviews/{reviewId}/decision")
    public ResponseEntity<?> decideReview(@PathVariable Long reviewId,
                                           @RequestParam boolean approve,
                                           @RequestBody(required = false) Map<String, String> body) {
        try {
            String note = body == null ? "" : body.get("note");
            Review review = reviewService.decideReview(reviewId, approve, note);
            return ResponseEntity.ok(Map.of("message",
                    approve ? "Review approved" : "Review rejected",
                    "review", new ReviewResponse(review)));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    private Map<String, Object> cropMap(Crop crop) {
        return Map.of(
                "id", crop.getId(),
                "name", crop.getName(),
                "category", crop.getCategory() == null ? "" : crop.getCategory(),
                "quantityKg", crop.getQuantityKg(),
                "pricePerKg", crop.getPricePerKg(),
                "approved", crop.isApproved(),
                "farmerId", crop.getFarmer() == null ? 0L : crop.getFarmer().getId(),
                "farmerName", crop.getFarmer() == null ? "" : crop.getFarmer().getName()
        );
    }
}
