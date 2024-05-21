package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {


    @Autowired
    private UserRepository userRepository;

    @GetMapping("fpt-dorm/profile")
    public String profile(@RequestParam("email") String email, Model model) {
        List<User> users = userRepository.searchByEmail(email);
        if (users != null) {
            model.addAttribute("user", users.getFirst());
            return "profile";
        } else {
            return "user_not_found";
        }
    }
}