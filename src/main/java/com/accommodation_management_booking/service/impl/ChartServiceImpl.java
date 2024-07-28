package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.service.ChartService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChartServiceImpl implements ChartService {

    private final BookingRepository bookingRepository;

    public ChartServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
//
//    @Override
//    public Map<String, Long> getBookingStatusCounts() {
//        List<Object[]> results = bookingRepository.countBookingsByStatus();
//        Map<String, Long> statusCounts = new HashMap<>();
//        for (Object[] result : results) {
//            Booking.Status status = (Booking.Status) result[0];
//            Long count = (Long) result[1];
//            statusCounts.put(status.name(), count);
//        }
//        return statusCounts;
//    }

    @Override
    public List<Object[]> findDistinctMonths() {
        return bookingRepository.findDistinctMonths();
    }

    @Override
    public List<Booking> findBookingsByMonth(int year, int month) {
        return bookingRepository.findBookingsByMonth(year, month);
    }

    @Override
    public Map<String, Long> getBookingStatusCountsForMonth(int year, int month) {
        List<Object[]> results = bookingRepository.countBookingsByStatus(year, month);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] result : results) {
            Booking.Status status = (Booking.Status) result[0];
            Long count = (Long) result[1];
            statusCounts.put(status.name(), count);
        }
        return statusCounts;
    }

}
