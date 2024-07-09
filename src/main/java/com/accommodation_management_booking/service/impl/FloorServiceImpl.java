package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.DormRepository;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.repository.RoomRepository;
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
    private DormRepository dormRepository;

    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Floor> getFloorsByDormId(int dormId) {
        return floorRepository.findByDormDormId(dormId);
    }

    @Override
    public List<FloorBedUsage> getFloorBedUsageByDormId(Integer dormId) {
        Dorm dorm = dormRepository.findById(dormId).orElseThrow(() -> new IllegalArgumentException("Dorm not found with id: " + dormId));

        List<Floor> floors = floorRepository.findByDormDormId(dormId);
        return floors.stream().map(floor -> {
            int totalBeds = 0;
            int usedBeds = 0;
            List<Room> rooms = roomRepository.findByFloorFloorId(floor.getFloorId()); // Fetch rooms by floor ID
            for (Room room : rooms) {
                List<Bed> beds = bedRepository.findByRoomRoomId(room.getRoomId()); // Fetch beds by room ID
                totalBeds += beds.size();
                usedBeds += beds.stream().filter(bed -> !bed.getIsAvailable()).count();
            }
            return new FloorBedUsage(floor, dorm, dormId, totalBeds, usedBeds, totalBeds - usedBeds);
        }).collect(Collectors.toList());
    }




    @Override
    public Floor addFloor(int dormId) {
        // Lấy Dorm từ dormId
        Dorm dorm = dormRepository.findById(dormId).orElseThrow(() -> new IllegalArgumentException("Invalid dormId: " + dormId));

        // Lấy số lượng tầng hiện tại trong dorm
        int currentFloorCount = floorRepository.countByDorm(dorm);

        // Tạo đối tượng Floor mới
        Floor newFloor = new Floor();
        newFloor.setDorm(dorm);
        newFloor.setFloorNumber(currentFloorCount + 1);

        // Lưu đối tượng Floor vào cơ sở dữ liệu
        return floorRepository.save(newFloor);
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
        Floor floor = floorRepository.findById(floorId).orElse(null);
        if (floor != null) {
            floorRepository.delete(floor);
        } else {
            throw new IllegalArgumentException("Floor not found with id: " + floorId);
        }
    }

    @Override
    public Floor getFloorById(int floorId) {
        return floorRepository.findById(floorId).orElse(null);
    }


    @Override
    public void updateFloor(Floor floor) {
        // Kiểm tra xem tầng có tồn tại trong database không
        Floor existingFloor = floorRepository.findById(floor.getFloorId()).orElse(null);
        if (existingFloor == null) {
            throw new IllegalArgumentException("Floor not found with id: " + floor.getFloorId());
        }

        // Kiểm tra xem tên tầng mới có trùng với các tầng khác trong cùng một dorm không
        if (isFloorNumberDuplicateInDorm(floor.getDorm().getDormId(), floor.getFloorNumber(), floor.getFloorId())) {
            throw new IllegalArgumentException("Floor number already exists for this dorm.");
        }

        // Giữ nguyên dormId
        floor.setDorm(existingFloor.getDorm());

        // Lưu thông tin tầng cập nhật vào database
        floorRepository.save(floor);
    }
    @Override
    public boolean isFloorNumberDuplicateInDorm(Integer dormId, Integer floorNumber, Integer floorIdToExclude) {
        List<Floor> existingFloors = floorRepository.findByDormDormId(dormId);

        for (Floor existingFloor : existingFloors) {
            if (existingFloor.getFloorNumber().equals(floorNumber) && !existingFloor.getFloorId().equals(floorIdToExclude)) {
                return true; // Found a duplicate floor number (excluding the current floorId)
            }
        }

        return false; // No duplicate floor number found
    }



    @Override
    public Floor getHighestFloorByDormId(int dormId) {
        return floorRepository.findTopByDormDormIdOrderByFloorNumberDesc(dormId);
    }


}
