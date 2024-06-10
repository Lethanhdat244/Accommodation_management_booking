package com.accommodation_management_booking.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private int roomId;

    private String roomStatus;

    private String roomNumber;

    private int capacity;

    private String status;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}