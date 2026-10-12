
package com.ruralconnect.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(precision = 10, scale = 2)
    private BigDecimal wholesalePricePerKg;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount25Kg;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount50Kg;

    @Column(precision = 10, scale = 2)
    private BigDecimal discount100Kg;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    public Crop() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(BigDecimal quantityKg) {
        this.quantityKg = quantityKg;
    }

    public BigDecimal getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getWholesalePricePerKg() {
        return wholesalePricePerKg;
    }

    public void setWholesalePricePerKg(BigDecimal wholesalePricePerKg) {
        this.wholesalePricePerKg = wholesalePricePerKg;
    }

    public BigDecimal getDiscount25Kg() {
        return discount25Kg;
    }

    public void setDiscount25Kg(BigDecimal discount25Kg) {
        this.discount25Kg = discount25Kg;
    }

    public BigDecimal getDiscount50Kg() {
        return discount50Kg;
    }

    public void setDiscount50Kg(BigDecimal discount50Kg) {
        this.discount50Kg = discount50Kg;
    }

    public BigDecimal getDiscount100Kg() {
        return discount100Kg;
    }

    public void setDiscount100Kg(BigDecimal discount100Kg) {
        this.discount100Kg = discount100Kg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }
}