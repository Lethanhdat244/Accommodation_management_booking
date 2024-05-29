package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class LoginAndRegistrationController {

    private UserService userService;

    @GetMapping("register")
    public String registerUser(Model model) {
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") UserDTO userDTO,
                               BindingResult result,
                               Model model,
                               @RequestParam("avatar") MultipartFile[] avatars,
                               @RequestParam("frontface") MultipartFile[] frontCccdImages,
                               @RequestParam("backface") MultipartFile[] backCccdImages) {

        try {
            userService.saveUser(userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "There was an error registering the user. Please try again.");
            return "register";
        }
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
