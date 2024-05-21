package com.accommodation_management_booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int userId;

    @NotNull
    @Size(max = 50)
    private String username;

    @NotNull
    @Size(max = 255)
    private String password;

    private Integer roleNumber;

    private String gender;

    private LocalDate birthdate;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 255)
    private String address;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 12)
    private String cccdNumber;

    @Size(max = 255)
    private String avatar;

    @Size(max = 255)
    private String frontCccdImage;

    @Size(max = 255)
    private String backCccdImage;

    private boolean isActive;
}