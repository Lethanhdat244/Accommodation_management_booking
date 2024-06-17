package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.dto.FloorDTO;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.DormRepository;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
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
    private DormRepository dormRepository;

    @Autowired
    private BedRepository bedRepository;

    @Override
    public List<Floor> getFloorsByDormId(Integer dormId) {
        return floorRepository.findByDormDormId(dormId);
    }

    @Override
    public List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId) {
        Dorm dorm = dormRepository.findById(dormId).orElse(null); // Assuming you have a DormRepository
        if (dorm == null) {
            throw new IllegalArgumentException("Dorm not found with id: " + dormId);
        }

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
            return new FloorBedUsage(floor, dorm, dormId, totalBeds, usedBeds, totalBeds - usedBeds);
        }).collect(Collectors.toList());
    }



    @Override
    public Floor addFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    public boolean isFloorNumberDuplicate(Integer dormId, Integer floorNumber) {
        // Retrieve floors for the dorm
        List<Floor> existingFloors = floorRepository.findByDormDormId(dormId);

        // Check if any of the existing floors have the same floor number
        for (Floor existingFloor : existingFloors) {
            if (existingFloor.getFloorNumber().equals(floorNumber)) {
                return true; // Found a duplicate floor number
            }
        }

        return false; // No duplicate floor number found
    }

    @Override
    public void deleteFloor(int floorId) {
        floorRepository.deleteById(floorId);
    }

    @Override
    public Floor getFloorById(int floorId) {
        return floorRepository.findById(floorId).orElse(null);
    }
    @Override
    public void updateFloor(Floor floor) {
        Floor existingFloor = floorRepository.findById(floor.getFloorId()).orElse(null);
        if (existingFloor == null) {
            throw new IllegalArgumentException("Floor not found with id: " + floor.getFloorId());
        }

        // Giữ nguyên dormId
        floor.setDorm(existingFloor.getDorm());

        floorRepository.save(floor);
    }
}
