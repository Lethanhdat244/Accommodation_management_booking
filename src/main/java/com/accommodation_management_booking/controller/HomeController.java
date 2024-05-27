package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {


    @GetMapping("home")
    public String homepage(){
        return "index";
    }


    @GetMapping("contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("index")
    public String index(){
        return "index";
    }

    @GetMapping("blog")
    public String blog(){
        return "blog";
    }

    @GetMapping("buy")
    public String buy(){
        return "buy";
    }

    @GetMapping("properties")
    public String properties(){
        return "properties";
    }

    @GetMapping("property_details")
    public String propertyDetails(){
        return "property_details";
    }

    @GetMapping("rent")
    public String rent(){
        return "rent";
    }

    @GetMapping("view-list")
    public String viewList(){
        return "view-list";
    }

}
