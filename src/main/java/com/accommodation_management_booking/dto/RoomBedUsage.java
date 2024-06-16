package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBedUsage  {
    private Room room;
    private int totalBeds;
    private int usedBeds;
    private int unusedBeds;
}