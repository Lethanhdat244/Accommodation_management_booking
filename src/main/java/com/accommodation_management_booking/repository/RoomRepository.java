package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r WHERE r.roomNumber LIKE %:keyword%")
    List<Room> searchByRoomNumber(@Param("keyword") String keyword);
}

