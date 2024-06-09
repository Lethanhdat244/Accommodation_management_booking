package com.accommodation_management_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewDTO {
    private Long newsId;
    private String title;
    private String content;
    private String imageUrl;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
