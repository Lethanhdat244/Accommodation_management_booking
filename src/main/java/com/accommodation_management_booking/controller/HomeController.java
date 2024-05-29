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

    @GetMapping("admin_homepage")
    public String admin_homepage(){
        return "admin_homepage";
    }

    @GetMapping("admin_list_student")
    public String admin_list_student(){
        return "admin_list_student";
    }

    @GetMapping("admin_add_student")
    public String admin_add_student(){
        return "admin_add_student";
    }

    @GetMapping("admin_list_room")
    public String admin_list_room(){
        return "admin_list_room";
    }

    @GetMapping("admin_payment_list")
    public String admin_payment_list(){
        return "admin_payment_list";
    }

    @GetMapping("admin_add_new_type_room")
    public String admin_add_new_type_room(){
        return "admin_add_new_type_room";
    }

    @GetMapping("admin_add_new_room")
    public String admin_add_new_room(){
        return "admin_add_new_room";
    }
}
