package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.entity.Feedback;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.FeedbackRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.impl.FeedbackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class FeedbackController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @GetMapping("/fpt-dorm/user/feedbackform")
    public String feedback(Model model, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<String> roomNumbers = bookingRepository.findRoomNumbersByUserId(user.getUserId());
        model.addAttribute("room", roomNumbers);
        return "/user/feedback";
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            String email = oauth2Token.getPrincipal().getAttribute("email");
            return userRepository.searchUserByEmail(email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.searchUserByEmail(userDetails.getUsername());
        } else {
            return null;
        }
    }

    @PostMapping("/fpt-dorm/user/feedback")
    public String submitFeedback(Model model,
                                 @RequestParam("title") String title,
                                 @RequestParam("rating") int rating,
                                 @RequestParam("content") String content,
                                 @RequestParam("selectedRoom") String roomNumber,
                                 Authentication authentication) {

        User user = getUserFromAuthentication(authentication);
        Integer bookingId = feedbackService.findLatestBookingIdByRoomNumber(roomNumber);

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setTitle(title);
        feedback.setRating(rating);
        feedback.setComment(content);
        feedback.setStatus(Feedback.Status.Pending);
        feedback.setCreatedAt(LocalDateTime.now());

        if (bookingId != null) {
            feedback.setBooking(bookingRepository.findById(bookingId).orElse(null));
        }

        feedbackService.saveFeedback(feedback);


        return "/homepage";
    }

    @GetMapping("/employee/all-feedback")
    public String FeedbackList(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "1") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = feedbackService.getallfeeback(pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", "");

        return "employee/employee_feedback";
    }


    @GetMapping("/fpt-dorm/employee/all_feedback/search")
    public String search(Model model,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         @RequestParam(defaultValue = "email,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<User> userPage;

        if (keyword == null || keyword.isEmpty()) {
            userPage = feedbackService.getallfeeback(pageable);
        } else {
            userPage = feedbackService.searchfeebackByMail(keyword, pageable);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);

        return "employee/employee_feedback";
    }


    @GetMapping("/fpt-dorm/employee/all-feedback/id={id}")
    public String feedbackdetail(Model model,
                                 @PathVariable("id") int userId,


                                 Pageable pageable) {
        Pageable pageableRequest = PageRequest.of(pageable.getPageNumber(), 2);
        Page<Feedback> feedbackPage = feedbackService.searchFeedbackByUser(userId, pageableRequest);

        model.addAttribute("userId", userId);
        model.addAttribute("feedbackdetail", feedbackPage);
        return "employee/emplyee_feedbackDetaill";
    }

    @GetMapping("/fpt-dorm/employee/all_feedback/search_detail")
    public String searchfeedbackdetail(Model model,
                                       @RequestParam(value = "keyword") String keyword
            , Pageable pageable
    ) {

        Pageable pageableRequest = PageRequest.of(pageable.getPageNumber(), 2);


        if (keyword == null || keyword.isEmpty()) {
            model.addAttribute("mess", "lỗi ");
        } else {
            Page<Feedback> feedbacksearch;

            feedbacksearch = feedbackService.searchFeedbackBytilte(keyword, pageableRequest);

            model.addAttribute("feedbackdetail", feedbacksearch);
            model.addAttribute("keyword", keyword);

        }


        return "employee/emplyee_feedbackDetaill";
    }


        @GetMapping("/fpt-dorm/employee/view_detail_feedback/id={feedbackId}")
        public String viewFeedbackDetail(@PathVariable("feedbackId") int feedbackId, Model model) {
            FeedbackDTO feedbackAndUser = feedbackService.findFeedbackAndUserByFeedbackId(feedbackId);
            model.addAttribute("feedback", feedbackAndUser);
            return "employee/employee_feedback_detail_user";
        }

//    @GetMapping("/fpt-dorm/employee/update_feedback_status/id={feedbackId}")
//    public String updateFeedbackStatus(@PathVariable("feedbackId") int feedbackId, Model model) {
//        String message = feedbackService.updateFeedbackStatus(feedbackId);
//        model.addAttribute("message", message);
//
//        return "employee/test";
//    }
@PostMapping("/fpt-dorm/employee/update_feedback_status/id={feedbackId}")
public ResponseEntity<String> updateFeedbackStatus(@PathVariable("feedbackId") int feedbackId) {
    System.out.println("Received feedback update request for feedback ID: " + feedbackId);
    Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);

    if (feedbackOpt.isPresent()) {
        Feedback feedback = feedbackOpt.get();
        Feedback.Status currentStatus = feedback.getStatus();
        switch (currentStatus) {
            case Pending:
                feedback.setStatus(Feedback.Status.Replied);
                break;
            case Replied:
                return ResponseEntity.ok("Feedback has already been Replied.");
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Status updated successfully");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Feedback not found");
}


}








