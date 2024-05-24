package com.accommodation_management_booking.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/profile/view")
    public String UserProfile(Model model){
        return getUserProfileView();
    }

    private static String getUserProfileView() {
        return "userProfileView";
    }
}
