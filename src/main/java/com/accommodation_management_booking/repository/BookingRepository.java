package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT DISTINCT b.room.roomNumber FROM Booking b WHERE b.user.userId = :userId")
    List<String> findRoomNumbersByUserId(@Param("userId") int userId);

    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.roleUser = 'USER'")
    Page<Booking> findAllByRoleUser(Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.username = :username AND u.roleUser = 'USER'")
    Booking findByUsernameAndRoleUser(@Param("username") String username);

    @Query("SELECT b FROM Booking b WHERE b.bookingId = :id")
    List<Booking> findByBookingId(@Param("id") int id);

    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.username LIKE %:username%")
    List<Booking> findByUserName(@Param("username") String username);

    @Query("SELECT b FROM Booking b JOIN b.user u WHERE u.username LIKE %:keyword% OR b.bookingId = :id")
    List<Booking> searchAllBy(@Param("keyword") String keyword, @Param("id") int id);

    @Query("SELECT b FROM Booking b JOIN Bed bd ON b.bed.bedId = bd.bedId WHERE b.user.userId = :userId ORDER BY b.checkOutDate DESC")
    Page<Booking> findByUserIdOrderByCheckOutDateDescWithBedInfo(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.room r JOIN b.user u WHERE r.roomNumber LIKE %:roomNumber% AND u.userId = :userId AND u.roleUser = 'USER' ORDER BY b.checkOutDate DESC")
    Page<Booking> findByRoomNumberContainingAndUserId(@Param("roomNumber") String roomNumber, @Param("userId") int userId, Pageable pageable);

    @Query("SELECT MAX(b.bookingId) FROM Booking b WHERE b.room.roomNumber = :roomNumber")
    Integer findLatestBookingIdByRoomNumber(@Param("roomNumber") String roomNumber);
}
