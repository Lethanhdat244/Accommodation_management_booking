package com.accommodation_management_booking.service;


import com.accommodation_management_booking.dto.BookingDTO;
import com.accommodation_management_booking.dto.RoomDTO;

import java.util.List;

public interface BookingService {
    List<RoomDTO> getAllRoom();
    void saveBooking(BookingDTO bookingDTO);
}
