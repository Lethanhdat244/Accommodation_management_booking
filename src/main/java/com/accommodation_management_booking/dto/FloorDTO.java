package com.accommodation_management_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloorDTO {
    private Integer floorId;
    private Integer dormId;
    private Integer floorNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
