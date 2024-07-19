package com.accommodation_management_booking.service;

import com.accommodation_management_booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    public int countBookingsInCurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.countBookingsInCurrentMonth(now);
    }
}
