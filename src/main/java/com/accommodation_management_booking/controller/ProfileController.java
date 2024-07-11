package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public String profile(Model model) {
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }

        // Add the email to the model
        model.addAttribute("email", email);

        // Fetch the user profile using the email
        List<User> users = userRepository.searchByEmail(email);
        if (users != null && !users.isEmpty()) {
            model.addAttribute("user", users.get(0));
            return "user_profile";
        } else {
            return "error/error";
        }
    }

    @GetMapping("fpt-dorm/admin/profile")
    public String admin_profile(Model model) {
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }

        // Add the email to the model
        model.addAttribute("email", email);

        // Fetch the user profile using the email
        List<User> users = userRepository.searchByEmail(email);
        if (users != null && !users.isEmpty()) {
            model.addAttribute("user", users.get(0));
            return "admin/admin-profile";
        } else {
            return "error/error";
        }
    }

    @GetMapping("fpt-dorm/employee/profile")
    public String employee_profile(Model model) {
// Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }

        // Add the email to the model
        model.addAttribute("email", email);

        // Fetch the user profile using the email
        List<User> users = userRepository.searchByEmail(email);
        if (users != null && !users.isEmpty()) {
            model.addAttribute("user", users.get(0));
            return "employee/employee-profile";
        } else {
            return "error/error";
        }
    }

    @GetMapping("/fpt-dorm/user/edit-profile")
    public String showEditProfileForm(Model model) {
        // Lấy thông tin của người dùng từ cơ sở dữ liệu bằng email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }model.addAttribute("email", email);
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);
        return "user/editUserProfile";
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
        return "redirect:/fpt-dorm/user/profile";
    }


    @GetMapping("/fpt-dorm/admin/edit-profile")
    public String showEditAdminProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }model.addAttribute("email", email);
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
            return "admin/admin-profile";
        }
        return "redirect:/fpt-dorm/admin/edit-profile?success";
    }

    @GetMapping("/fpt-dorm/employee/edit-profile")
    public String showEditEmployeeProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        // Extract email from the authentication object
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        } else {
            // Handle cases where the authentication is not OAuth2 or UserDetails
            email = "Unknown";
        }model.addAttribute("email", email);
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
            return "employee/employee-profile";
        }
        return "redirect:/fpt-dorm/employee/edit-profile?success";
    }


}
