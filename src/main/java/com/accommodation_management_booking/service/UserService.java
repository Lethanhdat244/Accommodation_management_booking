package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void saveUser(UserDTO userDTO, MultipartFile[] avatars, MultipartFile[] frontCccdImages, MultipartFile[] backCccdImages);

    void completeUserProfile(UserDTO userDTO, MultipartFile avatar, MultipartFile frontCccdImage, MultipartFile backCccdImage);

    void processForgotPassword(String email);

    void resetPassword(String token, String newPassword);

    boolean changePassword(String currentPassword, String newPassword);

    User findByEmail(String email);
}
