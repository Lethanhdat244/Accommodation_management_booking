package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.dto.FeedbackDTO;
import com.accommodation_management_booking.dto.ResidentHistoryDTO;
import com.accommodation_management_booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT DISTINCT b.room.roomNumber FROM Booking b WHERE b.user.userId = :userId  AND b.status = 'Confirmed' ")
    List<String> findRoomNumbersByUserId(@Param("userId") int userId);
@Query("SELECT DISTINCT b.room.roomNumber, b.startDate, b.endDate FROM Booking b WHERE b.user.userId = :userId AND b.status = 'Confirmed' ")
List<FeedbackDTO> findRoomDetailsByUserId(@Param("userId") int userId);



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

    @Query("SELECT b FROM Booking b JOIN Bed bd ON b.bed.bedId = bd.bedId WHERE b.user.userId = :userId  AND b.status = 'Confirmed' ORDER BY b.endDate DESC")
    Page<Booking> findByUserIdOrderByCheckOutDateDescWithBedInfo(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT b FROM Booking b JOIN b.room r JOIN b.user u WHERE r.roomNumber LIKE %:roomNumber% AND u.userId = :userId AND u.roleUser = 'USER'  AND b.status = 'Confirmed' ORDER BY b.endDate DESC")
    Page<Booking> findByRoomNumberContainingAndUserId(@Param("roomNumber") String roomNumber, @Param("userId") int userId, Pageable pageable);

    @Query("SELECT MAX(b.bookingId) FROM Booking b WHERE b.room.roomNumber = :roomNumber")
    Integer findLatestBookingIdByRoomNumber(@Param("roomNumber") String roomNumber);

    @Query("SELECT b FROM Booking b JOIN b.room r JOIN b.user u WHERE r.roomNumber LIKE %:roomNumber% AND u.roleUser = 'USER'")
    Page<Booking> findByRoomNumberContainingAndRoleUser(@Param("roomNumber") String roomNumber, Pageable pageable);

    @Query("SELECT DISTINCT MONTH(b.startDate), YEAR(b.startDate) FROM Booking b ORDER BY YEAR(b.startDate) DESC, MONTH(b.startDate) DESC")
    List<Object[]> findDistinctMonths();

    @Query("SELECT b FROM Booking b WHERE YEAR(b.startDate) = :year AND MONTH(b.startDate) = :month")
    List<Booking> findBookingsByMonth(int year, int month);

    @Query("SELECT b.status, COUNT(b) FROM Booking b WHERE YEAR(b.bookingDate) = :year AND MONTH(b.bookingDate) = :month GROUP BY b.status")
    List<Object[]> countBookingsByStatus(int year, int month);

    @Query("SELECT COUNT(b) FROM Booking b WHERE MONTH(b.bookingDate) = MONTH(:now) AND YEAR(b.bookingDate) = YEAR(:now)")
    int countBookingsInCurrentMonth(LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.room.floor.floorId = :floorId")
    List<Booking> findByRoomFloorFloorId(@Param("floorId") int floorId);

    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId")
    List<Booking> findByRoomRoomId(@Param("roomId") int roomId);

    long countByRoomRoomId(Integer roomId);

    Booking findByUserUserIdAndStatus(Integer userId, Booking.Status status);
}
