package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorBedUsage {
    private Floor floor;
    private Dorm dorm;
    private int dormId; // Thêm trường dormId
    private int totalBeds;
    private int usedBeds;
    private int freeBeds;


}


