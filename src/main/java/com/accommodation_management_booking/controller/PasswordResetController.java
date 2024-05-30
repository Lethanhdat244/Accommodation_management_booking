package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class PasswordResetController {

    private final UserService userService;

    @GetMapping("/fpt-dorm/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/fpt-dorm/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        try {
            userService.resetPassword(token, password);
            model.addAttribute("message", "Your password has been reset successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "reset_password";
        }
        return "redirect:/fpt-dorm/reset-password?success";
    }
}
