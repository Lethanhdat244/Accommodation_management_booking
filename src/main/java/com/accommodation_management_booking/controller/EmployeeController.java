package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {
    @GetMapping("fpt-dorm/employee/home")
    public String employee_homepage(){
        return "employee_homepage";
    }
    @GetMapping("fpt-dorm/employee/students-list")
    public String employee_student(@AuthenticationPrincipal OAuth2User oauth2User,
                                   @AuthenticationPrincipal UserDTO user,
                                   Model model)
    {
        return "employee_student";
    }
    @GetMapping("fpt-dorm/employee/complain")
    public String employee_complain(){return "employee_complain";}
}
