package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceBookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private Float quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PaymentMethod {
        CreditCard, DebitCard, BankQRCode
    }
}