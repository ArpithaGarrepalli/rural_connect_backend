package com.ruralconnect.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruralconnect.backend.entity.Order;
import com.ruralconnect.backend.entity.OrderStatus;
import com.ruralconnect.backend.entity.Review;
import com.ruralconnect.backend.entity.ReviewStatus;
import com.ruralconnect.backend.entity.Role;
import com.ruralconnect.backend.entity.User;
import com.ruralconnect.backend.repository.CropRepository;
import com.ruralconnect.backend.repository.OrderRepository;
import com.ruralconnect.backend.repository.ReviewRepository;
import com.ruralconnect.backend.repository.UserRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository,
                         UserRepository userRepository, CropRepository cropRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cropRepository = cropRepository;
    }

    @Transactional
    public Review submitReview(Long customerId, Long orderId, Integer rating, String comment) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be from 1 to 5");
        }
        if (comment != null && comment.length() > 1000) {
            throw new IllegalArgumentException("Review comment must be 1000 characters or fewer");
        }
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER || !customer.isActive()) {
            throw new IllegalArgumentException("An active customer account is required");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("You can only review your own orders");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("You can review an order only after it is delivered");
        }
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("A review has already been submitted for this order");
        }

        Review review = new Review();
        review.setCustomer(customer);
        review.setCrop(order.getCrop());
        review.setOrder(order);
        review.setRating(rating);
        review.setComment(comment == null ? "" : comment.trim());
        review.setStatus(ReviewStatus.PENDING);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatus(ReviewStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<Review> getApprovedReviewsForCrop(Long cropId) {
        if (!cropRepository.existsById(cropId)) throw new IllegalArgumentException("Crop not found");
        return reviewRepository.findByCropIdAndStatus(cropId, ReviewStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public List<Review> getFarmerReviews(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new IllegalArgumentException("Farmer not found"));
        if (farmer.getRole() != Role.FARMER) throw new IllegalArgumentException("Farmer account required");
        return reviewRepository.findByCropFarmerId(farmerId);
    }

    @Transactional
    public Review decideReview(Long reviewId, boolean approve, String note) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new IllegalArgumentException("This review has already been moderated");
        }
        if (note != null && note.length() > 500) {
            throw new IllegalArgumentException("Admin note must be 500 characters or fewer");
        }
        review.setStatus(approve ? ReviewStatus.APPROVED : ReviewStatus.REJECTED);
        review.setAdminNote(note == null ? "" : note.trim());
        return reviewRepository.save(review);
    }
}
