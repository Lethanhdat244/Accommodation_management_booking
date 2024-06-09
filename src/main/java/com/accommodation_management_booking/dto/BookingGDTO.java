package com.accommodation_management_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingGDTO {
    private int bookingId;
    private int userId;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal deposit;
    private BigDecimal totalAmount;
    private String status;
    private String username;
    private String phoneNumber;
    private String roomStatus;
    private String roomNumber;
    private int capacity;
}
