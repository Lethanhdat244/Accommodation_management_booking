package com.accommodation_management_booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

}
