package com.accommodation_management_booking.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackUserDTO {
    private int feedbackId;
    private String roomNumber;
    private int rating;
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    @Email
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    private String avatar;



}
