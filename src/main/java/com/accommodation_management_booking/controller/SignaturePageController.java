package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.service.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.util.Base64;

@Controller
public class SignaturePageController {

    @GetMapping("/fpt-dorm/signature")
    public String showSignaturePage(Model model, @RequestParam String token) {
        if (TokenService.isTokenUsed(token)) {
            return "error/403"; // Trang lỗi nếu token đã được sử dụng
        }

        Integer bookingId = TokenService.decode(token);
        model.addAttribute("bookingId", bookingId);

        // Đánh dấu token là đã được sử dụng
        TokenService.markTokenAsUsed(token);

        return "user_signature";
    }
}

