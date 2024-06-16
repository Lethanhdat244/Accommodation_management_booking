package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.RoomBedUsage;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private FloorRepository floorRepository;

    @Override
    public List<RoomBedUsage> getRoomBedUsageByFloorId(Integer floorId) {
        Floor floor = floorRepository.findById(floorId).orElseThrow(() -> new IllegalArgumentException("Invalid floor ID"));

        return floor.getRooms().stream().map(room -> {
            int totalBeds = room.getBeds().size();
            int freeBeds = (int) room.getBeds().stream().filter(Bed::getIsAvailable).count();
            int usedBeds = totalBeds - freeBeds;
            return new RoomBedUsage(room, totalBeds, freeBeds, usedBeds);
        }).collect(Collectors.toList());
    }
}
