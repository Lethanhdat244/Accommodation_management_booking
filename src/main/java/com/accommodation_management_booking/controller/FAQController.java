package com.accommodation_management_booking.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FAQController {
    @GetMapping("/fpt-dorm/user/faq")
    public String faq(Model model, HttpSession session){
        model.addAttribute("role", session.getAttribute("role"));
        return "student_faq";
    }
}
