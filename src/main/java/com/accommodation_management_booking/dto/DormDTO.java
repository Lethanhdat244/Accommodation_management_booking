package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Dorm.DormGender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DormDTO {
    private Integer dormId;
    private String dormName;
    private DormGender dormGender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
