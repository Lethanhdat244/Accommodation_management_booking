package com.accommodation_management_booking.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private int bookingId;

    private int userId;

    private int roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @DecimalMin("0.00")
    private BigDecimal deposit;

    @DecimalMin("0.00")
    private BigDecimal totalAmount;

    private String status;

}
