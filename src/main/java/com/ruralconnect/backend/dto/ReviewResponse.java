package com.ruralconnect.backend.dto;

import java.time.LocalDateTime;

import com.ruralconnect.backend.entity.Review;

public class ReviewResponse {
    private Long id;
    private Long orderId;
    private Long cropId;
    private String cropName;
    private Long customerId;
    private String customerName;
    private String farmerName;
    private Integer rating;
    private String comment;
    private String status;
    private String adminNote;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        id = review.getId();
        orderId = review.getOrder() != null ? review.getOrder().getId() : null;
        cropId = review.getCrop() != null ? review.getCrop().getId() : null;
        cropName = review.getCrop() != null ? review.getCrop().getName() : null;
        customerId = review.getCustomer() != null ? review.getCustomer().getId() : null;
        customerName = review.getCustomer() != null ? review.getCustomer().getName() : null;
        farmerName = review.getCrop() != null && review.getCrop().getFarmer() != null
                ? review.getCrop().getFarmer().getName() : null;
        rating = review.getRating();
        comment = review.getComment();
        status = review.getStatus() != null ? review.getStatus().name() : null;
        adminNote = review.getAdminNote();
        createdAt = review.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getCropId() { return cropId; }
    public String getCropName() { return cropName; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getFarmerName() { return farmerName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public String getAdminNote() { return adminNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
