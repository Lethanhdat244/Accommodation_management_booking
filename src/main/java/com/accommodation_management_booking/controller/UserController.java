package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String UserProfile(Model model) {
        try {
            UserDTO user = userService.getUser(1);
            model.addAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "User not found or an error occurred while retrieving the user.");
        }
        return "userProfileView";  // Tên của template view (userProfileView.html)
    }
    @GetMapping("/change-password")
    public String ChangePassword(Model model) {
        return "changePassword";
    }
}
