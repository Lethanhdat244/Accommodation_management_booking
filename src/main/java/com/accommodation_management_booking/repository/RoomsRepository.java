package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomsRepository extends JpaRepository<Rooms, Integer> {
    @Query("SELECT c FROM Rooms c")
    List<Rooms> getAllRooms();
}
