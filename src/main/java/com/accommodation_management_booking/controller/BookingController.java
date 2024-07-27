package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.config.GenderMapper;
import com.accommodation_management_booking.entity.*;
import com.accommodation_management_booking.repository.*;
import com.accommodation_management_booking.service.PaypalService;
import com.paypal.base.rest.APIContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    @Autowired
    private DormRepository dormRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaypalService paypalService;

    @Autowired
    private APIContext apiContext;

    private Integer getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userRepository.findByEmail(userDetails.getUsername());
                return user.getUserId(); // Assuming userId is the field name
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String email = oauth2User.getAttribute("email");
                User user = userRepository.findByEmail(email);
                return user.getUserId();
            }
        }
        throw new IllegalStateException("User not found in context");
    }

    @GetMapping("fpt-dorm/user/booking")
    public String booking(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = getLoggedInUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        if(!user.isProfileComplete()){
            return "redirect:/fpt-dorm/profile/complete";
        }

        Booking existingBooking = bookingRepository.findByUserUserIdAndStatus(userId, Booking.Status.Confirmed);
        if (existingBooking != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You already have a confirmed booking.");
            return "redirect:/fpt-dorm/user/booking-error";
        }

        model.addAttribute("role", session.getAttribute("role"));
        return "user/booking_type_room";
    }

    @GetMapping("fpt-dorm/user/booking-error")
    public String bookingError(Model model) {
        return "user/booking_error";
    }

    @PostMapping("fpt-dorm/user/booking/select")
    public String selectRoomType(@RequestParam("roomType") String roomType, Model model, HttpSession session) {
        Integer userId = getLoggedInUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Dorm.DormGender dormGender = GenderMapper.map(user.getGender());

        List<Dorm> dorms = dormRepository.findByDormGender(dormGender);
        model.addAttribute("roomType", roomType);
        model.addAttribute("dorms", dorms);
        model.addAttribute("userId", userId);
        model.addAttribute("role", session.getAttribute("role"));
        return "user/booking_details";
    }

    @PostMapping("fpt-dorm/user/booking/import")
    public String importRoomType(@RequestParam("roomType") String roomType, Model model) {
        model.addAttribute("roomType", roomType);
        return "user/import";
    }

    @GetMapping("fpt-dorm/user/booking/floors")
    @ResponseBody
    public List<Floor> getFloors(@RequestParam("dormId") Integer dormId) {
        return floorRepository.findByDormDormId(dormId);
    }

    @GetMapping("fpt-dorm/user/booking/rooms")
    @ResponseBody
    public List<Room> getRooms(@RequestParam("floorId") Integer floorId, @RequestParam("capacity") Integer capacity) {
        return roomRepository.findByFloorFloorIdAndCapacity(floorId, capacity);
    }

    @GetMapping("fpt-dorm/user/booking/roomsByFloor")
    @ResponseBody
    public List<Room> getRooms(@RequestParam("floorId") Integer floorId) {
        return roomRepository.findByFloorFloorId(floorId);
    }

    @GetMapping("/fpt-dorm/user/roominused")
    @ResponseBody
    public List<Room> getRoomOccurpied(@RequestParam("floorId") Integer floorId) {
        return roomRepository.getRoomInUsed(floorId);

    }

    @GetMapping("fpt-dorm/user/booking/beds")
    @ResponseBody
    public List<Bed> getBeds(@RequestParam("roomId") Integer roomId) {
        return bedRepository.findByRoomRoomIdAndIsAvailableTrueAndMaintenanceStatus(roomId, Bed.MaintenanceStatus.Available);
    }

    @PostMapping("fpt-dorm/user/booking/confirm")
    public String confirmBooking(@RequestParam("bed") Integer bedId,
                                 @RequestParam("room") Integer roomId,
                                 @RequestParam("checkin") LocalDate checkinDate,
                                 @RequestParam("checkout") LocalDate checkoutDate,
                                 @RequestParam("totalPrice") Float totalPrice) {

        Integer userId = getLoggedInUserId();
        Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Booking booking = new Booking();
        booking.setBed(bed);
        booking.setRoom(room);
        booking.setUser(user);
        booking.setStartDate(checkinDate);
        booking.setEndDate(checkoutDate);
        booking.setTotalPrice(totalPrice);

        bookingRepository.save(booking);

        bed.setIsAvailable(false);
        bedRepository.save(bed);

        return "user/booking_confirmation";
    }

    @GetMapping("fpt-dorm/user/booking/assignRoomAndBed")
    @ResponseBody
    public ResponseEntity<?> assignRoomAndBed(@RequestParam("floorId") Integer floorId, @RequestParam("capacity") Integer capacity) {
        List<Room> rooms = roomRepository.findByFloorFloorId(floorId);
        Room assignedRoom = null;
        Bed assignedBed = null;

        // Tìm phòng đã có người và còn giường trống
        for (Room room : rooms) {
            long bookedBedsCount = bookingRepository.countByRoomRoomId(room.getRoomId());
            if (bookedBedsCount > 0 && room.getCapacity() - bookedBedsCount >= capacity) {
                assignedRoom = room;
                assignedBed = bedRepository.findFirstByRoomRoomIdAndIsAvailableTrueAndMaintenanceStatus(room.getRoomId(), Bed.MaintenanceStatus.Available);
                if (assignedBed != null) {
                    break;
                }
            }
        }

        // Nếu không tìm thấy, tìm phòng trống
        if (assignedRoom == null || assignedBed == null) {
            for (Room room : rooms) {
                long bookedBedsCount = bookingRepository.countByRoomRoomId(room.getRoomId());
                if (bookedBedsCount == 0 && room.getCapacity() >= capacity) {
                    assignedRoom = room;
                    assignedBed = bedRepository.findFirstByRoomRoomIdAndIsAvailableTrueAndMaintenanceStatus(room.getRoomId(), Bed.MaintenanceStatus.Available);
                    if (assignedBed != null) {
                        break;
                    }
                }
            }
        }

        // Nếu không tìm thấy phòng và giường phù hợp
        if (assignedRoom == null || assignedBed == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available room or bed found.");
        }

        // Trả về thông tin phòng và giường đã được xếp
        Map<String, Object> response = new HashMap<>();
        response.put("roomId", assignedRoom.getRoomId());
        response.put("roomNumber", assignedRoom.getRoomNumber());
        response.put("bedId", assignedBed.getBedId());
        response.put("bedName", assignedBed.getBedName());

        return ResponseEntity.ok(response);
    }
}
