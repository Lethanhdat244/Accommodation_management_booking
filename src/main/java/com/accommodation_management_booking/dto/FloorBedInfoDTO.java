package com.accommodation_management_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorBedInfoDTO {
    private Integer floorNumber;
    private Integer totalBeds;
    private Integer usedBeds;
    private Integer availableBeds;

}
