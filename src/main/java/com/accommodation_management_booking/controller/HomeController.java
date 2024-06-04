package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/fpt-dorm")
    public String redirectToHome() {
        return "redirect:/fpt-dorm/home";
    }

    @GetMapping("fpt-dorm/home")
    public String homepage(){
        return "homepage";
    }

    @GetMapping("fpt-dorm/home-user")
    public String booking(){
        return "home_user";
    }

    @GetMapping("fpt-dorm/home/about")
    public String about(){
        return "about";
    }

    @GetMapping("fpt-dorm/home/gallery")
    public String gallery(){
        return "gallery";
    }

    @GetMapping("fpt-dorm/home/dinning")
    public String dinning(){
        return "dinning";
    }

    @GetMapping("fpt-dorm/home/new")
    public String news(){
        return "new";
    }

    @GetMapping("fpt-dorm/home/contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("fpt-dorm/home-user/rule")
    public String rule(){
        return "rule";
    }
}
