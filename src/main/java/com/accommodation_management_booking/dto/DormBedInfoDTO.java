package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Dorm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormBedInfoDTO {
    private Integer dormId;
    private String dormName;
    private Dorm.DormGender dormGender;
    private Integer totalBeds;
    private Integer usedBeds;
    private Integer availableBeds;

    public DormBedInfoDTO(Integer dormId, String dormName, Integer totalBeds, Integer usedBeds, Integer availableBeds) {
        this.dormId = dormId;
        this.dormName = dormName;
        this.totalBeds = totalBeds;
        this.usedBeds = usedBeds;
        this.availableBeds = availableBeds;
    }
}
