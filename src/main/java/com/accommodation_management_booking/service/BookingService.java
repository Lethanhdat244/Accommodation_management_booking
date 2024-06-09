package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.BookingGDTO;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;


    @Autowired
    private RoomRepository roomRepository;

    public List<BookingGDTO> getAllBookingDetails() {
        List<BookingGDTO> bookingDetails = new ArrayList<>();

        List<Booking> bookings = bookingRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        for (Booking booking : bookings) {
            BookingGDTO detail = new BookingGDTO();
            detail.setBookingId(booking.getBookingId());
            detail.setCheckInDate(booking.getCheckInDate());
            detail.setCheckOutDate(booking.getCheckOutDate());
            detail.setDeposit(booking.getDeposit());
            detail.setTotalAmount(booking.getTotalAmount());
            detail.setStatus(booking.getStatus().toString()); // Chuyển Enum Status thành String

            // Tìm thông tin user tương ứng
            User user = booking.getUser();
            detail.setUsername(user.getUsername());
            detail.setPhoneNumber(user.getPhoneNumber());

            // Tìm thông tin room tương ứng
            for (Room room : rooms) {
                if (room.getRoomId() == booking.getRoomId()) {
                    detail.setRoomNumber(room.getRoomNumber());
                    detail.setRoomStatus(room.getRoomStatus() != null ? room.getRoomStatus().name() : "UNKNOWN");
                    detail.setCapacity(room.getCapacity());
                    break;
                }
            }

            bookingDetails.add(detail);
        }

        return bookingDetails;
    }
    public BookingGDTO getBookingDetailById(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            BookingGDTO detail = new BookingGDTO();
            // Lấy thông tin booking
            detail.setBookingId(booking.getBookingId());
            detail.setCheckInDate(booking.getCheckInDate());
            detail.setCheckOutDate(booking.getCheckOutDate());
            detail.setDeposit(booking.getDeposit());
            detail.setTotalAmount(booking.getTotalAmount());
            detail.setStatus(booking.getStatus().toString());

            // Lấy thông tin user
            User user = booking.getUser();
            detail.setUserId(user.getUserId());
            detail.setUsername(user.getUsername());
            detail.setPhoneNumber(user.getPhoneNumber());

            // Lấy thông tin room
            Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
            if (room != null) {
                detail.setRoomId(room.getRoomId());
                detail.setRoomNumber(room.getRoomNumber());
                detail.setRoomStatus(room.getRoomStatus() != null ? room.getRoomStatus().name() : "UNKNOWN");
                detail.setCapacity(room.getCapacity());
            }

            return detail;
        }
        return null;
    }

}
