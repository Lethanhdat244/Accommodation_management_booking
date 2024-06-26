package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_booking")
public class ServiceBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceBookingId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private int serviceId;

    private int quantity;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}