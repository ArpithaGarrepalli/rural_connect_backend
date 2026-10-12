package com.ruralconnect.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ruralconnect.backend.entity.Order;

public class OrderResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long cropId;
    private String cropName;
    private String farmerName;
    private BigDecimal quantityKg;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private String cancellationReason;
    private String cancellationDecisionNote;
    private LocalDateTime cancellationRequestedAt;
    private LocalDateTime cancellationDecidedAt;

    public OrderResponse() {}

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.customerId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        this.customerName = order.getCustomer() != null ? order.getCustomer().getName() : null;
        this.cropId = order.getCrop() != null ? order.getCrop().getId() : null;
        this.cropName = order.getCrop() != null ? order.getCrop().getName() : null;
        this.farmerName = order.getCrop() != null && order.getCrop().getFarmer() != null
                ? order.getCrop().getFarmer().getName() : null;
        this.quantityKg = order.getQuantityKg();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus() != null ? order.getStatus().name() : null;
        this.createdAt = order.getCreatedAt();
        this.cancellationReason = order.getCancellationReason();
        this.cancellationDecisionNote = order.getCancellationDecisionNote();
        this.cancellationRequestedAt = order.getCancellationRequestedAt();
        this.cancellationDecidedAt = order.getCancellationDecidedAt();
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public Long getCropId() { return cropId; }
    public String getCropName() { return cropName; }
    public String getFarmerName() { return farmerName; }
    public BigDecimal getQuantityKg() { return quantityKg; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCancellationReason() { return cancellationReason; }
    public String getCancellationDecisionNote() { return cancellationDecisionNote; }
    public LocalDateTime getCancellationRequestedAt() { return cancellationRequestedAt; }
    public LocalDateTime getCancellationDecidedAt() { return cancellationDecidedAt; }
}
