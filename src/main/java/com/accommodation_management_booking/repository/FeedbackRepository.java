package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.entity.Feedback;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("SELECT DISTINCT u FROM User u " +
            "INNER JOIN Booking b ON u.userId = b.user.userId " +
            "INNER JOIN Feedback p ON b.bookingId = p.booking.bookingId " +
            "WHERE u.roleUser = 'USER'")
    Page<User> findUsersWithBooking(Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN Booking b ON u.userId = b.user.userId " +
            "JOIN Feedback p ON b.bookingId = p.booking.bookingId " +
            "WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    Page<User> searchByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT f FROM Feedback f WHERE f.user.userId = :userId")
    Page<Feedback> findByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT p FROM Feedback p " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Feedback> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query("SELECT new com.accommodation_management_booking.dto.FeedbackDTO(f.feedbackId, u.username, u.email, u.phoneNumber, f.status, u.userId, b.bookingId, f.rating, f.comment, f.createdAt, f.title, r.roomId, r.roomNumber) " +
            "FROM Feedback f JOIN f.user u JOIN f.booking b JOIN b.room r WHERE f.feedbackId = :feedbackId")
    FeedbackDTO findFeedbackAndUserByFeedbackId(@Param("feedbackId") int feedbackId);

    @Query("SELECT f FROM Feedback f WHERE f.feedbackId = :feedbackId")
    Optional findByFeedbackId(@Param("feedbackId") int feedbackId);

    @Query("SELECT f FROM Feedback f " +
            "JOIN f.booking b " +
            "JOIN b.room r " +
            "WHERE r.roomNumber IS NOT NULL")
    Page<Feedback> findFeedbackDetails(Pageable pageable);
}
