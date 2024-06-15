package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.entity.Floor;

import java.util.List;

public interface FloorService {
    public List<Floor> getFloorsByDormId(Integer dormId);
    List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId);
}
