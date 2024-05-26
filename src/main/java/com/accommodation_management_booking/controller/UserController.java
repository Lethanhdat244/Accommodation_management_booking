package com.accommodation_management_booking.controller;

import ch.qos.logback.core.model.Model;
import com.accommodation_management_booking.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;
    @GetMapping("/profile/view")
    public String UserProfile(Model model){

        return "userProfileView";
    }
    @GetMapping ("/change-password")
    public String ChangePassword(Model model){
        return "changePassword";
    }
}
