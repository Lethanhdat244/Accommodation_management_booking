package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
public class ProfileCompletionController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/profile/complete")
    public String showCompleteProfileForm(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user != null && !user.isProfileComplete()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            model.addAttribute("user", userDTO);
            return "completeProfile";
        }
        return "redirect:/home";
    }

    @PostMapping("/profile/complete")
    public String completeProfile(@ModelAttribute("user") UserDTO userDTO,
                                  @RequestParam("avatar") MultipartFile avatar,
                                  @RequestParam("frontCccdImage") MultipartFile frontCccdImage,
                                  @RequestParam("backCccdImage") MultipartFile backCccdImage,
                                  @AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            userService.completeUserProfile(userDTO, avatar, frontCccdImage, backCccdImage);
        }
        return "redirect:/home";
    }
}
