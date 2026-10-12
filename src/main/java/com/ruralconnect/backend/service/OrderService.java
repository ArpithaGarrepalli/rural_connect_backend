package com.ruralconnect.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruralconnect.backend.entity.Crop;
import com.ruralconnect.backend.entity.Order;
import com.ruralconnect.backend.entity.OrderStatus;
import com.ruralconnect.backend.entity.Role;
import com.ruralconnect.backend.entity.User;
import com.ruralconnect.backend.repository.CropRepository;
import com.ruralconnect.backend.repository.OrderRepository;
import com.ruralconnect.backend.repository.UserRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        CropRepository cropRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cropRepository = cropRepository;
    }

    @Transactional
    public Order placeOrder(Long customerId, Long cropId, BigDecimal quantityKg) {
        if (quantityKg == null || quantityKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        if (customer.getRole() != Role.CUSTOMER || !customer.isActive()) {
            throw new IllegalArgumentException("An active customer account is required");
        }

        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found"));
        if (!crop.isApproved()) throw new IllegalArgumentException("Crop is not approved");
        if (crop.getQuantityKg() == null || crop.getQuantityKg().compareTo(quantityKg) < 0) {
            throw new IllegalArgumentException("Not enough crop stock available");
        }

        BigDecimal total = crop.getPricePerKg().multiply(quantityKg);
        Order order = new Order();
        order.setCustomer(customer);
        order.setCrop(crop);
        order.setQuantityKg(quantityKg);
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        crop.setQuantityKg(crop.getQuantityKg().subtract(quantityKg));
        cropRepository.save(crop);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getFarmerOrders(Long farmerId) {
        return orderRepository.findByCropFarmerId(farmerId);
    }

    @Transactional
    public Order requestCancellation(Long customerId, Long orderId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Please provide a cancellation reason");
        }
        if (reason.trim().length() > 500) {
            throw new IllegalArgumentException("Cancellation reason must be 500 characters or fewer");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("This order does not belong to this customer");
        }
        if (order.getStatus() != OrderStatus.PLACED &&
            order.getStatus() != OrderStatus.CONFIRMED &&
            order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalArgumentException("This order can no longer be cancelled");
        }

        order.setStatusBeforeCancellation(order.getStatus());
        order.setStatus(OrderStatus.CANCELLATION_REQUESTED);
        order.setCancellationReason(reason.trim());
        order.setCancellationDecisionNote(null);
        order.setCancellationRequestedAt(LocalDateTime.now());
        order.setCancellationDecidedAt(null);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getPendingCancellationRequests() {
        return orderRepository.findByStatus(OrderStatus.CANCELLATION_REQUESTED);
    }

    @Transactional
    public Order decideCancellation(Long orderId, boolean approve, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() != OrderStatus.CANCELLATION_REQUESTED) {
            throw new IllegalArgumentException("This order has no pending cancellation request");
        }

        String decisionNote = note == null ? "" : note.trim();
        if (decisionNote.length() > 500) {
            throw new IllegalArgumentException("Decision note must be 500 characters or fewer");
        }

        if (approve) {
            Crop crop = order.getCrop();
            crop.setQuantityKg(crop.getQuantityKg().add(order.getQuantityKg()));
            cropRepository.save(crop);
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancellationDecisionNote(
                    decisionNote.isEmpty() ? "Cancellation approved by admin" : decisionNote);
        } else {
            OrderStatus previous = order.getStatusBeforeCancellation();
            if (previous == null || previous == OrderStatus.CANCELLATION_REQUESTED ||
                previous == OrderStatus.CANCELLED) {
                previous = OrderStatus.PLACED;
            }
            order.setStatus(previous);
            order.setCancellationDecisionNote(
                    decisionNote.isEmpty() ? "Cancellation rejected by admin" : decisionNote);
        }
        order.setCancellationDecidedAt(LocalDateTime.now());
        order.setStatusBeforeCancellation(null);
        return orderRepository.save(order);
    }
}
