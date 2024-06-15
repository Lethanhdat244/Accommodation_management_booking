package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
