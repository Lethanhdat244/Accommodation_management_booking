package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.ComplaintDTO;
import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Console;
import java.util.List;

@Controller
public class RequestController {
    UserRepository userRepository;
    ComplainRepository complainRepository;
    @GetMapping("fpt-dorm/home-user/my-request")
    public String studentRequest(@AuthenticationPrincipal OAuth2User oauth2User,
                                 @AuthenticationPrincipal UserDTO user,
                                 Model model) {
        String email = null;

        if (oauth2User != null) {
            email = oauth2User.getAttribute("email");
        } else if (user != null) {
            email = user.getEmail();
        }
        User onlineUser;
        if (email != null) {
            onlineUser = userRepository.findByEmail(email);
        }
        else {
            return "redirect:/fpt-dorm/login";
        }
        List<Complaint> complaints = complainRepository.findByUserUserId(onlineUser.getUserId());
        model.addAttribute("complainList", complaints);
        return "student_request";
    }
}
