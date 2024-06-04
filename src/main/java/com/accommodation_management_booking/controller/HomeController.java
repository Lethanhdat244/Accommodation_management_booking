package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
    public String booking(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
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
