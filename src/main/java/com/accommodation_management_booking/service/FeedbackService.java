package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.entity.Feedback;

public interface FeedbackService {
    void saveFeedback(Feedback feedback);

    Integer findLatestBookingIdByRoomNumber(String roomNumber);

//    FeedbackDTO findFeedbackAndUserByFeedbackId(int feedbackId);
}
