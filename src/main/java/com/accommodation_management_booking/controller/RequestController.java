package com.accommodation_management_booking.controller;

//import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.repository.UserRepository;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Console;
import java.util.List;

@Controller
public class RequestController {
    @Autowired
    ComplainRepository complainRepository;
    @Autowired
    UserRepository userRepository;
    @GetMapping("fpt-dorm/home-user/my-request")
    public String studentRequest(Authentication authentication, Model model) {
        UserDetails userDetails;
        String email;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
            try {
                List<User> users = userRepository.search1ByEmail(email);
                    List<Complaint> complainList = complainRepository.getRequestByUserEmail(users.getFirst().getEmail());
                    model.addAttribute("complaintDTOList", complainList);
                    model.addAttribute("contentTemplate", "student_request");
                return "base/base_user";
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
            try {
                List<User> users = userRepository.search1ByEmail(userDetails.getUsername());
                    List<Complaint> complainList = complainRepository.getRequestByUserEmail(users.getFirst().getEmail());
                    model.addAttribute("complaintDTOList", complainList);
                    model.addAttribute("contentTemplate", "student_request");
                return "base/base_user";
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
            return "login";
        }
        return "base/base_user";
    }
}
