package com.accommodation_management_booking.controller;

//import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.impl.ComplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RequestController {
    @Autowired
    ComplainRepository complainRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplainService complainService;

    User user;
    @GetMapping("fpt-dorm/user/news/my-request")
    public String studentRequest(Model model, Authentication authentication) {
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
        try {
            List<Complaint> complainList = complainRepository.getRequestsByUserId(user.getUserId());
            model.addAttribute("complaintDTOList", complainList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "student_request";
    }

    @PostMapping ("fpt-dorm/home-user/send-request")
    public String studentRequest(Model model
            ,@RequestParam("title") String title
            ,@RequestParam("content") String content
            , Authentication authentication){
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
        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setRoomId(1);
        complaint.setDescription(content);
        complaint.setStatus(Complaint.Status.Open);
        try {
            complainService.saveComplain(complaint);
            try {
                List<Complaint> complainList = complainRepository.getRequestsByUserId(user.getUserId());
                model.addAttribute("complaintDTOList", complainList);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
            List<Complaint> complainList = complainRepository.getRequestsByUserId(user.getUserId());
            model.addAttribute("complaintDTOList", complainList);
            return "student_request";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while creating complain. Please try again.");
            e.printStackTrace();
            List<Complaint> complainList = complainRepository.getRequestsByUserId(user.getUserId());
            model.addAttribute("complaintDTOList", complainList);
            return "student_request";
        }

        return "student_request";
    }
}
