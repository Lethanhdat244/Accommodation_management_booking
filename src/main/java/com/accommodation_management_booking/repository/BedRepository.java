package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Integer> {
    List<Bed> findByRoomRoomIdAndIsAvailableTrue(Integer roomId);

    List<Bed> findByRoomRoomIdAndIsAvailableTrueAndMaintenanceStatus(Integer roomId, Bed.MaintenanceStatus maintenanceStatus);
    List<Bed> findByRoom(Room room);

    List<Bed> findByRoom_Floor_Dorm_DormId(Integer dormId);

    Page<Bed> findByRoomRoomId(int roomId, Pageable pageable);

    Bed findByRoomAndBedName(Room room, String bedName);

    boolean existsByRoom_RoomIdAndBedName(int roomId, String bedName);

    List<Bed> findByRoomRoomId(Integer roomId);

    boolean existsByRoomRoomIdAndBedName(int roomId, String bedName);

    Bed findByBedName(String bedName);

    @Query("SELECT COUNT(b) FROM Bed b WHERE b.isAvailable = false AND b.maintenanceStatus = 'Available'")
    long countUsedBeds();

    Bed findFirstByRoomRoomIdAndIsAvailableTrueAndMaintenanceStatus(Integer roomId, Bed.MaintenanceStatus status);
}
