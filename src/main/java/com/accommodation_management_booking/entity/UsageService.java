package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usage_service")
public class UsageService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_service_id")
    private Integer usageServiceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "electricity")
    private Float electricity;

    @Column(name = "water")
    private Float water;

    @Column(name = "others")
    private Float others;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "quantity")
    private Float quantity;

    public enum PaymentMethod {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        BANK_QR_CODE("Bank QR Code");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static PaymentMethod fromDisplayName(String displayName) {
            for (PaymentMethod method : PaymentMethod.values()) {
                if (method.getDisplayName().equalsIgnoreCase(displayName)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("No enum constant with displayName " + displayName);
        }

        @Override
        public String toString() {
            return this.displayName;
        }
    }
}

