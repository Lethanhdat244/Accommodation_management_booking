package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.entity.Floor;

import java.util.List;

public interface FloorService {
    public List<Floor> getFloorsByDormId(Integer dormId);
    List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId);

    Floor addFloor(Floor floor);

    boolean isFloorNumberDuplicate(Integer dormId, Integer floorNumber);

    Floor getFloorById(int floorId);

    void deleteFloor(int floorId);

    void updateFloor(Floor floor);
}
