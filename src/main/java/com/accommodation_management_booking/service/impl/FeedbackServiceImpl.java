package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.entity.Feedback;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.FeedbackRepository;
import com.accommodation_management_booking.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public Integer findLatestBookingIdByRoomNumber(String roomNumber) {
        return bookingRepository.findLatestBookingIdByRoomNumber(roomNumber);
    }

    public Page<User> getallfeeback(Pageable pageable) {
        return feedbackRepository.findUsersWithBooking(pageable);
    }


    public Page<User> searchfeebackByMail(String keyword, Pageable pageable) {
        return feedbackRepository.searchByEmail(keyword, pageable);
    }


    public Page<Feedback> searchFeedbackByUser(int userId, Pageable pageable) {
        return feedbackRepository.findByUserId(userId, pageable);
    }

    public Page<Feedback> searchFeedbackBytilte(String keyword, Pageable pageable) {
        return feedbackRepository.searchByTitle(keyword, pageable);
    }

    public FeedbackDTO findFeedbackAndUserByFeedbackId(int feedbackId) {
        return feedbackRepository.findFeedbackAndUserByFeedbackId(feedbackId);
    }


}
