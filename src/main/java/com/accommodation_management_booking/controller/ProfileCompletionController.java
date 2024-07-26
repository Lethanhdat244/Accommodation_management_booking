package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.EmailService;
import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@AllArgsConstructor
public class ProfileCompletionController {

    private final UserRepository userRepository;
    private final UserService userService;
    private EmailService emailService;

    @GetMapping("/fpt-dorm/profile/complete")
    public String showCompleteProfileForm(Model model, @AuthenticationPrincipal Object principal) {
        String email = null;

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            email = userDetails.getUsername();
        }

        if (email != null) {
            User user = userRepository.findByEmail(email);
            if (user != null && !user.isProfileComplete()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(user.getEmail());
                model.addAttribute("user", userDTO);
                return "completeProfile";
            }
            return redirectToRoleBasedHome(user);
        }

        return "redirect:/fpt-dorm/login";
    }

    @PostMapping("/fpt-dorm/profile/complete")
    public String completeProfile(@ModelAttribute("user") UserDTO userDTO,
                                  @RequestParam("avatar") MultipartFile avatar,
                                  @RequestParam("frontCccdImage") MultipartFile frontCccdImage,
                                  @RequestParam("backCccdImage") MultipartFile backCccdImage,
                                  @AuthenticationPrincipal Object principal,
                                  Model model) {
        try {
            String email = null;

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                email = oAuth2User.getAttribute("email");
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                email = userDetails.getUsername();
            }

            if (email != null) {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    ExecutorService executor = Executors.newCachedThreadPool();

                    CompletableFuture<Void> profileCompletionTask = CompletableFuture.runAsync(() -> {
                        try {
                            userService.completeUserProfile(userDTO, avatar, frontCccdImage, backCccdImage);
                        } catch (Exception e) {
                            throw new RuntimeException("Error completing user profile", e);
                        }
                    }, executor);

                    CompletableFuture<Void> emailTask = CompletableFuture.runAsync(() -> {
                        String emailContent = String.format(
                                "<p>Thank you for completing your profile. Please enjoy our services.</p>" +
                                        "<p><a href=\"http://localhost:8080/fpt-dorm/home\">Go to Home</a></p>"
                        );
                        emailService.sendCompleteRegistrationEmail(userDTO.getEmail(), emailContent);
                    }, executor);

                    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(profileCompletionTask, emailTask);
                    combinedFuture.join();

                    executor.shutdown();
                    return redirectToRoleBasedHome(user);
                }
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while completing your profile. Please try again.");
            return "completeProfile";
        }

        return "redirect:/fpt-dorm/user/news";
    }

    private String redirectToRoleBasedHome(User user) {
        switch (user.getRoleUser()) {
            case ADMIN:
                return "redirect:/fpt-dorm/admin/home";
            case EMPLOYEE:
                return "redirect:/fpt-dorm/employee/home";
            case USER:
            default:
                return "redirect:/fpt-dorm/user/news";
        }
    }
}
