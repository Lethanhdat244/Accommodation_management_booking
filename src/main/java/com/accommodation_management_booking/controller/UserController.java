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

    @GetMapping("/profile/view")
    public String UserProfile(Model model) {
        // Mã để hiển thị trang người dùng
        return "userProfileView";  // Tên của template view (userProfileView.html)
    }

    @PostMapping("/find")
    public String get(@RequestParam(name = "id") int id, Model model) {
        try {
            UserDTO user = userService.getUser(id);
            model.addAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "User not found or an error occurred while retrieving the user.");
        }
        return "userProfileView";
    }
}
