package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("home")
    public String homepage(){
        return "homepage";
    }



}
