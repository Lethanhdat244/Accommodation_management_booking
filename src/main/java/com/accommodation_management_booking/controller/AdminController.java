package com.accommodation_management_booking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("fpt-dorm/admin/home")
    public String admin_homepage(Model model, Authentication authentication){
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        return "admin/admin_homepage";
    }

    @GetMapping("fpt-dorm/admin/admin_list_student")
    public String admin_list_students(){
        return "redirect:/fpt-dorm/admin/student/all-student";
    }

    @GetMapping("fpt-dorm/admin/admin_list_employees")
    public String admin_list_employees(){
        return "redirect:/fpt-dorm/admin/employee/all-employee";
    }

    @GetMapping("fpt-dorm/admin/admin_add_student")
    public String admin_add_student(){
        return "redirect:/fpt-dorm/admin/student/add";
    }

    @GetMapping("fpt-dorm/admin/admin_add_employee")
    public String admin_add_employee(){
        return "redirect:/fpt-dorm/admin/employee/add";
    }

    @GetMapping("fpt-dorm/admin/admin_list_room")
    public String admin_list_room(){
        return "admin_list_room";
    }

    @GetMapping("fpt-dorm/admin/admin_payment_list")
    public String admin_payment_list(){
        return "admin_payment_list";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_type_room")
    public String admin_add_new_type_room(){
        return "admin_add_new_type_room";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_room")
    public String admin_add_new_room(){
        return "admin_add_new_room";
    }

    @GetMapping("fpt-dorm/admin/admin_list_feedback")
    public String admin_list_feedback(){
        return "admin_list_feedback";
    }

    @GetMapping("fpt-dorm/admin/admin_list_complaint")
    public String admin_list_complaint(){
        return "admin_list_complaint";
    }

}
