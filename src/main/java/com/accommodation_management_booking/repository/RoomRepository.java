package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
