package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BedRepository bedRepository;

    @Override
    public List<Floor> getFloorsByDormId(Integer dormId) {
        return floorRepository.findByDormDormId(dormId);
    }

    @Override
    public List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId) {
        List<Floor> floors = floorRepository.findByDormDormId(dormId);
        return floors.stream().map(floor -> {
            int totalBeds = 0;
            int usedBeds = 0;
            for (Room room : floor.getRooms()) {
                for (Bed bed : room.getBeds()) {
                    totalBeds++;
                    if (!bed.getIsAvailable()) {
                        usedBeds++;
                    }
                }
            }
            return new FloorBedUsage(floor,totalBeds, usedBeds, totalBeds - usedBeds);
        }).collect(Collectors.toList());
    }
}
