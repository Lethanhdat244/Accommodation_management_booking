package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void saveUser(UserDTO userDTO, MultipartFile[] avatars, MultipartFile[] frontCccdImages, MultipartFile[] backCccdImages);

}
