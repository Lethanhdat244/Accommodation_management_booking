package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.service.EmailService;
import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class LoginAndRegistrationController {

    private UserService userService;
    private EmailService emailService;

    @GetMapping("fpt-dorm/register")
    public String registerUser(Model model) {
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("fpt-dorm/register/save")
    public String registration(@ModelAttribute("user") UserDTO userDTO,
                               Model model) {

        try {
            userService.saveUsers(userDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "There was an error registering the user. Please try again.");
            return "register";
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:/fpt-dorm/register?success";
    }

    @GetMapping("/fpt-dorm/login")
    public String login(){
        return "login";
    }

}
