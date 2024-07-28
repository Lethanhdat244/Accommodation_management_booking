package com.accommodation_management_booking.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.accommodation_management_booking.dto.FeedbackUserDTO;
import com.accommodation_management_booking.service.impl.FeedbackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @GetMapping("/fpt-dorm")
    public String redirectToHome() {
        return "redirect:/fpt-dorm/home";
    }

    @GetMapping("fpt-dorm/home")
    public String homepage() {
        return "homepage";
    }

    @GetMapping("fpt-dorm/home/room")
    public String room(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "2") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackUserDTO> feedbackPage = feedbackService.getAllFeedbackDetails(pageable);

        model.addAttribute("comments", feedbackPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedbackPage.getTotalPages());
        return "room";
    }

    @GetMapping("fpt-dorm/home/about")
    public String about() {
        return "about";
    }

    @GetMapping("fpt-dorm/home/gallery")
    public String gallery() {
        return "gallery";
    }


    @GetMapping("fpt-dorm/home/contact")
    public String contact(){
        return "contact";
    }

    @GetMapping("fpt-dorm/user/rule")
    public String rule(Model model, HttpSession session){
        model.addAttribute("role", session.getAttribute("role"));
        return "rule";
    }
}
