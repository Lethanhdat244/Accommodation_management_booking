package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private int feedbackId;
    private String username;
    private String email;
    private String phoneNumber;
    private Feedback.Status status;
    private int userId;
    private int bookingId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private String title;
    private int roomId;
    private String roomNumber;
}
