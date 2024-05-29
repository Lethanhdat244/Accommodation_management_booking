package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("home")
    public String homepage(){
        return "homepage_user";
    }

    @GetMapping("home/about")
    public String about(){
        return "about";
    }

    @GetMapping("home/gallery")
    public String gallery(){
        return "gallery";
    }

    @GetMapping("home/dinning")
    public String dinning(){
        return "dinning";
    }

    @GetMapping("home/new")
    public String news(){
        return "new";
    }

    @GetMapping("home/contact")
    public String contact(){
        return "contact";
    }
}
