package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.RoomBedUsage;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
    private FloorRepository floorRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BedRepository bedRepository;

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

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> getRoomsByFloorId(int floorId) {
        return roomRepository.findByFloorFloorId(floorId);
    }

    @Override
    public Room addRoomToFloor(int floorId, Room room) {
        Optional<Floor> floor = floorRepository.findById(floorId);
        if (floor.isPresent()) {
            room.setFloor(floor.get());
            return roomRepository.save(room);
        } else {
            throw new RuntimeException("Floor not found");
        }
    }

    @Override
    public Room getRoomById(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    @Override
    public void updateRoom(Room room) {
        // Check for unique room number within the same floor
        Room existingRoom = roomRepository.findByFloorAndRoomNumber(room.getFloor(), room.getRoomNumber());
        if (existingRoom != null && !existingRoom.getRoomId().equals(room.getRoomId())) {
            throw new IllegalArgumentException("Room number already exists on this floor.");
        }

        // Check for non-negative capacity
        if (room.getCapacity() < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }

        // Check for positive price per bed
        if (room.getPricePerBed() <= 0) {
            throw new IllegalArgumentException("Price per bed must be greater than zero.");
        }

        roomRepository.save(room);
    }


    @Override
    public void deleteRoom(int roomId) {
        roomRepository.deleteById(roomId);
    }

    @Override
    public boolean existsByRoomNumberAndFloorId(String roomNumber, int floorId) {
        return roomRepository.existsByRoomNumberAndFloorFloorId(roomNumber, floorId);
    }

    @Override
    public Room findByFloorAndRoomNumber(Floor floor, String roomNumber) {
        return roomRepository.findByFloorAndRoomNumber(floor, roomNumber);
    }


    @Override
    public boolean isBedNameDuplicate(String bedName, Room room) {
        return bedRepository.findByRoomAndBedName(room, bedName) != null;
    }
}
