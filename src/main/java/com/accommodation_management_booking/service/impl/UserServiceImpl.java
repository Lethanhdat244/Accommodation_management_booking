package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private Cloudinary cloudinary;

    @Override
    public void saveUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        if (userDTO.getRoleUser() == null) {
            user.setRoleUser(User.Role.USER);
        } else {
            user.setRoleUser(userDTO.getRoleUser());
        }
        user.setGender(userDTO.getGender());
        user.setBirthdate(userDTO.getBirthdate());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setCccdNumber(userDTO.getCccdNumber());

        try {
            Map uploadResult = cloudinary.uploader().upload(userDTO.getAvatar(), ObjectUtils.emptyMap());
            user.setAvatar(uploadResult.get("url").toString());

            uploadResult = cloudinary.uploader().upload(userDTO.getFrontCccdImage(), ObjectUtils.emptyMap());
            user.setFrontCccdImage(uploadResult.get("url").toString());

            uploadResult = cloudinary.uploader().upload(userDTO.getBackCccdImage(), ObjectUtils.emptyMap());
            user.setBackCccdImage(uploadResult.get("url").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        userRepository.save(user);
    }

}
