package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.entity.UsageService;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UsageServiceRepository;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UsageServiceController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsageServiceRepository usageServiceRepository;

    User user;

    @GetMapping("/fpt-dorm/user/usage-service")
    public String fptDormUsedServices(Model model,
                                      Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
            user = userRepository.searchUserByEmail(email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
            user = userRepository.searchUserByEmail(userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        UsageService u = usageServiceRepository.getCurrentUsageService(user.getUserId());
        model.addAttribute("usageService", u);
        List<UsageService> usageServiceList = usageServiceRepository.getUsageServicesByUserId(user.getUserId());
        model.addAttribute("usageServiceList", usageServiceList);
        return "student_usage_services";
    }
}
