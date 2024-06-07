package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsageServiceController {
    @GetMapping("/fpt-dorm/home-user/usage-service")
    public String fptDormUsedServices(Model model) {
        model.addAttribute("contentTemplate", "student_usage_services");
        return "base/base_user";
    }
}
