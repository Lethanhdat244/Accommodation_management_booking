package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDTO, MultipartFile[] avatars, MultipartFile[] frontCccdImages, MultipartFile[] backCccdImages);

    void completeUserProfile(UserDTO userDTO, MultipartFile avatar, MultipartFile frontCccdImage, MultipartFile backCccdImage);

    void processForgotPassword(String email);

    void resetPassword(String token, String newPassword);

    boolean changePassword(String currentPassword, String newPassword);

    User findByEmail(String email);

    Page<User> findAllUser(Pageable pageable);

    Page<User> searchAllByUser(String keyword, Pageable pageable);
    Page<User> searchByName(String name, Pageable pageable);
    Page<User> searchByEmail(String email, Pageable pageable);
    Page<User> searchByPhoneNumber(String phoneNumber, Pageable pageable);


    List<User> findAllEmployee();

    void updateUser(UserDTO userDTO, int id, MultipartFile[] avatars, MultipartFile[] frontCccdImages, MultipartFile[] backCccdImages);

    void deleteUser(int id);
}
