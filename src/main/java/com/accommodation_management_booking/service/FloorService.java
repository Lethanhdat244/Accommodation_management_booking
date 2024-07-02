package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.entity.Floor;

import java.util.List;

public interface FloorService {
    List<Floor> getFloorsByDormId(int dormId);
    List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId);

    public Floor addFloor(int dormId);

    boolean isFloorNumberDuplicate(Integer dormId, Integer floorNumber);

    Floor getFloorById(int floorId);

    void deleteFloor(int floorId);

    void updateFloor(Floor floor);

    boolean isFloorNumberDuplicateInDorm(Integer dormId, Integer floorNumber, Integer floorId);

    Floor getHighestFloorByDormId(int dormId);


}
