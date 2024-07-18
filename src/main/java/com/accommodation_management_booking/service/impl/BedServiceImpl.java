package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BedServiceImpl  implements BedService {

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    @Transactional
    public void addBedToRoom(int roomId, String bedName) {
        if (bedRepository.existsByRoomRoomIdAndBedName(roomId, bedName)) {
            throw new IllegalArgumentException("Bed with name " + bedName + " already exists in this room.");
        }
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // Create a new Bed
            Bed bed = new Bed();
            bed.setRoom(room);
            bed.setBedName(bedName);
            bed.setIsAvailable(true); // assuming new beds are always available
            bed.setMaintenanceStatus(Bed.MaintenanceStatus.Available);

            // Save the new bed
            bedRepository.save(bed);
        } else {
            throw new IllegalArgumentException("Room with id " + roomId + " not found");
        }
    }

    @Override
    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    @Override
    public Bed getBedById(int bedId) {
        Optional<Bed> optionalBed = bedRepository.findById(bedId);
        if (optionalBed.isPresent()) {
            return optionalBed.get();
        } else {
            throw new IllegalArgumentException("Bed with id " + bedId + " not found");
        }
    }

    @Override
    public Bed updateBed(Bed bed) {
        // Check and convert maintenance status if necessary
        if (bed.getMaintenanceStatus() == Bed.MaintenanceStatus.UnderMaintenance) {
            bed.setMaintenanceStatus(Bed.MaintenanceStatus.UnderMaintenance);
        }

        // Perform the update
        Bed existingBed = bedRepository.findById(bed.getBedId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));

        // Update fields
        existingBed.setRoom(bed.getRoom());
        existingBed.setBedName(bed.getBedName());
        existingBed.setIsAvailable(bed.getIsAvailable());
        existingBed.setMaintenanceStatus(bed.getMaintenanceStatus());

        // Save updated bed
        return bedRepository.save(existingBed);
    }

    @Override
    public void deleteBed(int bedId) {
        bedRepository.deleteById(bedId);
    }


    @Override
    public boolean bedNameExistsInRoom(int roomId, String bedName) {
        return bedRepository.existsByRoom_RoomIdAndBedName(roomId, bedName);
    }
    @Override
    public Bed getBedByName(String bedName) {
        return bedRepository.findByBedName(bedName); // Implement findByBedName in your repository
    }

    @Override
    public long countUsedBeds() {
        return bedRepository.countUsedBeds();
    }
}
