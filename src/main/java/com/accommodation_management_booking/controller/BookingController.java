package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.BookingGDTO;
import com.accommodation_management_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/fpt-dorm/admin")
public class BookingController {

    @Autowired
    private BookingService bookingService;



    @GetMapping("/list_booking")
    public String listBooking(Model model) {

        List<BookingGDTO> bookingDetails = bookingService.getAllBookingInfo();


        model.addAttribute("bookingInfo", bookingDetails);

        return "admin_list_booking";
    }

    @GetMapping("/admin_list_booking_detail/{bookingId}")
    public String getBookingDetail(@PathVariable int bookingId, Model model) {
        BookingGDTO bookingDetail = bookingService.getBookingDetailById(bookingId);
        model.addAttribute("bookingDetail", bookingDetail);
        return "admin_booking_detail";
    }



}
