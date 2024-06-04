package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.PasswordResetToken;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.PasswordResetTokenRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.EmailService;
import com.accommodation_management_booking.service.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private PasswordEncoder passwordEncoder;
    private PasswordResetTokenRepository tokenRepository;
    private EmailService emailService;

    @Override
    public void saveUser(UserDTO userDTO, MultipartFile[] avatars, MultipartFile[] frontCccdImages, MultipartFile[] backCccdImages) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists in the system.");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoleUser(userDTO.getRoleUser() != null ? userDTO.getRoleUser() : User.Role.USER);
        user.setGender(userDTO.getGender());
        user.setBirthdate(userDTO.getBirthdate());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setCccdNumber(userDTO.getCccdNumber());
        user.setProfileComplete(true);

        try {
            // Xử lý upload ảnh avatar
            for (MultipartFile avatar : avatars) {
                if (!avatar.isEmpty()) {
                    user.setAvatar(uploadImage(avatar));
                }
            }

            // Xử lý upload ảnh frontCccdImage
            for (MultipartFile frontCccdImage : frontCccdImages) {
                if (!frontCccdImage.isEmpty()) {
                    user.setFrontCccdImage(uploadImage(frontCccdImage));
                }
            }
            // Xử lý upload ảnh backCccdImage
            for (MultipartFile backCccdImage : backCccdImages) {
                if (!backCccdImage.isEmpty()) {
                    user.setBackCccdImage(uploadImage(backCccdImage));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading files", e);
        }

        userRepository.save(user);
    }

    @Override
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(24));
        tokenRepository.save(resetToken);
        String resetLink = "http://localhost:8080/fpt-dorm/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired password reset token.");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }


    @Override
    public void completeUserProfile(UserDTO userDTO, MultipartFile avatar, MultipartFile frontCccdImage, MultipartFile backCccdImage) {
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user != null && !user.isProfileComplete()) {
            try {
                user.setRoleUser(userDTO.getRoleUser() != null ? userDTO.getRoleUser() : User.Role.USER);
                user.setAvatar(uploadImage(avatar));
                user.setFrontCccdImage(uploadImage(frontCccdImage));
                user.setBackCccdImage(uploadImage(backCccdImage));
                user.setGender(userDTO.getGender());
                user.setBirthdate(userDTO.getBirthdate());
                user.setPhoneNumber(userDTO.getPhoneNumber());
                user.setAddress(userDTO.getAddress());
                user.setCccdNumber(userDTO.getCccdNumber());
            } catch (IOException e) {
                // Handle the exception appropriately
                e.printStackTrace();
                System.out.println("Lỗi");
                return; // Or handle the error as needed
            }
            user.setProfileComplete(true);
            userRepository.save(user);
        }
    }

    private String uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }
}

