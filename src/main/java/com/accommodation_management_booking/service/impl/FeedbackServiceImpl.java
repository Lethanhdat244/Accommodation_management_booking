package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.dto.FeedbackUserDTO;
import com.accommodation_management_booking.entity.Feedback;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.FeedbackRepository;
import com.accommodation_management_booking.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


//    public FeedbackDTO findFeedbackAndUserByFeedbackId(int feedbackId) {
//        return feedbackRepository.findFeedbackAndUserByFeedbackId(feedbackId);
//    }
    public Page<FeedbackUserDTO> getAllFeedbackDetails(Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepository.findFeedbackDetails(pageable);

        List<FeedbackUserDTO> feedbackUserDTOList = feedbackPage.getContent().stream()
                .map(feedback -> new FeedbackUserDTO(
                        feedback.getFeedbackId(),
                        feedback.getBooking().getRoom().getRoomNumber(),
                        feedback.getRating(),
                        feedback.getTitle(),
                        feedback.getComment(),
                        feedback.getCreatedAt(),
                        feedback.getUser().getEmail(),
                        feedback.getUser().getAvatar()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(feedbackUserDTOList, pageable, feedbackPage.getTotalElements());
    }




}
