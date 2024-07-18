package com.accommodation_management_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentHistoryDTO {
    private int userId;
    private String email;

    private String username;
    private String roomNumber;
    private String bedName;
    private String phoneNumber;

    private Float totalPrice;
    private Integer capacity;
    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;
}
