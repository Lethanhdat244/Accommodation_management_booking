package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.BookingDTO;
import com.accommodation_management_booking.dto.RoomDTO;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RoomDTO> getAllRoom() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream().map(room -> {
            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomId(room.getRoomId());
            roomDTO.setFloorId(room.getFloor().getFloorId());
            roomDTO.setRoomNumber(room.getRoomNumber());
            roomDTO.setCapacity(room.getCapacity());
            roomDTO.setPricePerBed(room.getPricePerBed());
            roomDTO.setCreatedAt(room.getCreatedAt());
            roomDTO.setUpdatedAt(room.getUpdatedAt());
            return roomDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveBooking(BookingDTO bookingDTO) {
        Bed bed = bedRepository.findById(bookingDTO.getBedId()).
                orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
        User user = userRepository.findById(bookingDTO.getUserId()).
                orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        if (!bed.getIsAvailable()) {
            throw new IllegalArgumentException("The selected bed is not available");
        }

        Booking booking = new Booking();
        booking.setBed(bed);
        booking.setUser(user);
        booking.setStartDate(bookingDTO.getCheckInDate());
        booking.setEndDate(bookingDTO.getCheckOutDate());
        booking.setTotalPrice(calculateTotalPrice(bookingDTO));
        booking.setStatus(Booking.Status.Pending);

        bookingRepository.save(booking);

    }

    private Float calculateTotalPrice(BookingDTO bookingDTO) {
        Room room = bedRepository.findById(bookingDTO.getBedId()).
                orElseThrow(() -> new IllegalArgumentException("Invalid bed ID")).getRoom();
        long days = bookingDTO.getCheckOutDate().toEpochDay() - bookingDTO.getCheckInDate().toEpochDay();
        return room.getPricePerBed() * days;
    }


}
