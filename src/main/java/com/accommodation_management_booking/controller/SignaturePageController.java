package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.util.Base64;

@Controller
public class SignaturePageController {
    @GetMapping("/fpt-dorm/signature")
    public String showSignaturePage(Model model, @RequestParam String token) {
        Integer bookingId = decode(token);
        model.addAttribute("bookingId", bookingId);
        return "user_signature";
    }

    public static int decode(String encodedId) {
        String idAsString = new String(Base64.getUrlDecoder().decode(encodedId));
        return Integer.parseInt(idAsString);
    }
}
