package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{id}")
    public String UserProfile(@PathVariable(name = "id") int id, Model model) {
        try {
            UserDTO user = userService.getUser(id);
            model.addAttribute("user", user);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "User not found or an error occurred while retrieving the user.");
        }
        return "userProfileView";  // Tên của template view (userProfileView.html)
    }
    @GetMapping("/password-permission")
    public String UserPasswordPermission(@RequestParam(name = "id")int id,Model model) {
        model.addAttribute("user_id", id);
        return "changePasswordRequest";
    }
    @PostMapping("/change-password")
    public String ChangePassword(@RequestParam(name = "user_id")int id,
                                 @RequestParam(name = "password") String password,
                                 @RequestParam(name = "NewPassword") String NewPassword,
                                 @RequestParam(name = "ReNewPassword") String ReNewPassword,
                                 Model model) {
            UserDTO user = userService.getUser(id);
            if(user.getPassword().equals(password)){
                if(NewPassword.equals(ReNewPassword)){
                    userService.updatePassword(id, NewPassword);
                    model.addAttribute("user_id", id);
                    model.addAttribute("succeedMessage", "Password changed.");
                    return "changePasswordRequest";
                }
                else {
                    model.addAttribute("user_id", id);
                    model.addAttribute("errorMessage", "Not match to new password.");
                    return "changePasswordRequest";
                }
            }
            else {
                model.addAttribute("user_id", id);
                model.addAttribute("errorMessage", "Password not match.");
                return "changePasswordRequest";
            }
    }

}
