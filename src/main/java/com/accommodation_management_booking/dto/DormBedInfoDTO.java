package com.accommodation_management_booking.dto;

import lombok.Data;

@Data
public class DormBedInfoDTO {
    private Integer dormId;
    private String dormName;
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
