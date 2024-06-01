package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequestController {
    @GetMapping("fpt-dorm/home-user/my-request")
    public String studentRequest() {
        return "student_request";
    }
}
