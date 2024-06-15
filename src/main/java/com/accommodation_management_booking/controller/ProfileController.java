package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.NewDTO;
import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.NewService;
import com.accommodation_management_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ProfileController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("fpt-dorm/user/profile")
    public String profile(@RequestParam("email") String email, Model model) {
        List<User> users = userRepository.searchByEmail(email);
        if (users != null) {
            model.addAttribute("user", users.getFirst());
            return "user_profile";
        } else {
            return "error/error";
        }
    }

    @GetMapping("fpt-dorm/admin/profile")
    public String admin_profile(@RequestParam("email") String email, Model model) {
        List<User> users = userRepository.searchByEmail(email);
        if (users != null) {
            model.addAttribute("user", users.getFirst());
            return "admin/admin_profile";

        } else {
            return "error/error";
        }
    }

    @GetMapping("fpt-dorm/employee/profile")
    public String employee_profile(@RequestParam("email") String email, Model model) {
        List<User> users = userRepository.searchByEmail(email);
        if (users != null) {
            model.addAttribute("user", users.getFirst());
            return "employee/employee_profile";

        } else {
            return "error/error";
        }
    }

    @GetMapping("/fpt-dorm/user/edit-profile/{email}")
    public String showEditProfileForm(@PathVariable("email") String email, Model model) {
        // Lấy thông tin của người dùng từ cơ sở dữ liệu bằng email
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "editUserProfile";
    }


    @PostMapping("/fpt-dorm/user/update-user-profile")
    public String saveEditUserProfile(@ModelAttribute("userDTO") UserDTO userDTO,
                                      @RequestParam("email") String email,
                                      Model model,
                                      @RequestParam("avatar") MultipartFile[] avatars,
                                      @RequestParam("frontface") MultipartFile[] frontCccdImages,
                                      @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            userService.updateUser1(email, userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating user. Please try again.");
            e.printStackTrace();
            return "user_profile";
        }
        return "redirect:/fpt-dorm/user/profile?email=" + email;
    }


    @GetMapping("/fpt-dorm/admin/edit-profile/{email}")
    public String showEditAdminProfile(@PathVariable("email") String email, Model model) {
        // Lấy thông tin của người dùng từ cơ sở dữ liệu bằng email
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "admin/editAdminProfile";
    }


    @PostMapping("/fpt-dorm/admin/update-user-profile")
    public String saveEditAdminProfile(@ModelAttribute("userDTO") UserDTO userDTO,
                                       @RequestParam("email") String email,
                                       Model model,
                                       @RequestParam("avatar") MultipartFile[] avatars,
                                       @RequestParam("frontface") MultipartFile[] frontCccdImages,
                                       @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            userService.updateUser1(email, userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating user. Please try again.");
            e.printStackTrace();
            return "admin/admin_profile";
        }
        return "redirect:/fpt-dorm/admin/edit-profile/" + email + "?success";
    }

    @GetMapping("/fpt-dorm/employee/edit-profile/{email}")
    public String showEditEmployeeProfile(@PathVariable("email") String email, Model model) {
        // Lấy thông tin của người dùng từ cơ sở dữ liệu bằng email
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "employee/editEmployeeProfile";
    }


    @PostMapping("/fpt-dorm/employee/update-user-profile")
    public String saveEditEmployeeProfile(@ModelAttribute("userDTO") UserDTO userDTO,
                                          @RequestParam("email") String email,
                                          Model model,
                                          @RequestParam("avatar") MultipartFile[] avatars,
                                          @RequestParam("frontface") MultipartFile[] frontCccdImages,
                                          @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            userService.updateUser1(email, userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating user. Please try again.");
            e.printStackTrace();
            return "employee/employee_profile";
        }
        return "redirect:/fpt-dorm/employee/edit-profile/" + email + "?success";
    }


}
