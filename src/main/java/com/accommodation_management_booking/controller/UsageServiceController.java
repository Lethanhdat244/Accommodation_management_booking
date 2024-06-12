package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsageServiceController {
    @GetMapping("/fpt-dorm/user/usage-service")
    public String fptDormUsedServices() {
        return "student_usage_services";
    }
}
