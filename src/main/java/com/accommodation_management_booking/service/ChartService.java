package com.accommodation_management_booking.service;

import com.accommodation_management_booking.entity.Booking;

import java.util.List;
import java.util.Map;

public interface ChartService {
//    Map<String, Long> getBookingStatusCounts();


    public List<Object[]> findDistinctMonths();

    public List<Booking> findBookingsByMonth(int year, int month);


    public Map<String, Long> getBookingStatusCountsForMonth(int year, int month);

}
