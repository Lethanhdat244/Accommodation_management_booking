package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignaturePageController {
    @GetMapping("/fpt-dorm/signature")
    public String showSignaturePage(@RequestParam("bookingId") int bookingId) {
        return "user_signature";
    }
}
