package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "bed_id", nullable = false)
    private Integer bedId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_price")
    private Float totalPrice;

    @Column(name = "amount_paid", columnDefinition = "float default 0")
    private Float amountPaid;

    @Column(name = "refund_amount", columnDefinition = "float default 0")
    private Float refundAmount;

    @Column(name = "refund_date")
    private LocalDate refundDate;

    @Column(name = "booking_date", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime bookingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "enum('Pending','Confirmed','Cancelled') default 'Pending'")
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "deposit", precision = 38, scale = 2)
    private BigDecimal deposit;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Column(name = "total_amount", precision = 38, scale = 2)
    private BigDecimal totalAmount;

    // Enum for status
    public enum Status {
        Pending,
        Confirmed,
        Cancelled
    }

}