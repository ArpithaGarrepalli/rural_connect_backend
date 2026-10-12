package com.ruralconnect.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantityKg;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    private OrderStatus statusBeforeCancellation;

    @Column(length = 500)
    private String cancellationReason;

    @Column(length = 500)
    private String cancellationDecisionNote;

    private LocalDateTime cancellationRequestedAt;
    private LocalDateTime cancellationDecidedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Order() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public BigDecimal getQuantityKg() { return quantityKg; }
    public void setQuantityKg(BigDecimal quantityKg) { this.quantityKg = quantityKg; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public OrderStatus getStatusBeforeCancellation() { return statusBeforeCancellation; }
    public void setStatusBeforeCancellation(OrderStatus statusBeforeCancellation) {
        this.statusBeforeCancellation = statusBeforeCancellation;
    }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public String getCancellationDecisionNote() { return cancellationDecisionNote; }
    public void setCancellationDecisionNote(String cancellationDecisionNote) {
        this.cancellationDecisionNote = cancellationDecisionNote;
    }

    public LocalDateTime getCancellationRequestedAt() { return cancellationRequestedAt; }
    public void setCancellationRequestedAt(LocalDateTime cancellationRequestedAt) {
        this.cancellationRequestedAt = cancellationRequestedAt;
    }

    public LocalDateTime getCancellationDecidedAt() { return cancellationDecidedAt; }
    public void setCancellationDecidedAt(LocalDateTime cancellationDecidedAt) {
        this.cancellationDecidedAt = cancellationDecidedAt;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
