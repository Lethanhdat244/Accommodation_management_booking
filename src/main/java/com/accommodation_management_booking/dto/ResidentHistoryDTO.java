package com.accommodation_management_booking.dto;

import jakarta.persistence.Column;
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
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
