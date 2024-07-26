package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserBookingDTO;
import com.accommodation_management_booking.entity.*;
import com.accommodation_management_booking.repository.*;
import com.accommodation_management_booking.service.BedService;
import com.accommodation_management_booking.service.BookingService;
import com.accommodation_management_booking.service.ChartService;
import com.accommodation_management_booking.service.UserService;
import com.accommodation_management_booking.service.impl.ComplainService;
import com.accommodation_management_booking.service.impl.UsageServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.accommodation_management_booking.entity.User.Role;

@Controller
public class AdminController {

    @Autowired
    ComplainRepository complainRepository;
    @Autowired
    ComplainService complainService;
    @Autowired
    DormRepository dormRepository;
    @Autowired
    UserBookingRepository userBookingRepository;
    @Autowired
    UsageServiceService usageServiceService;
    @Autowired
    UserRepository userRepository;

    private final ChartService chartService;

    private final UserService userService;

    private final BookingService bookingService;
    private final BedService bedService;
    private final ComplainRepository complaintRepository;
    @Autowired
    private BedRepository bedRepository;

    public AdminController(ChartService chartService, UserService userService, BookingService bookingService, BedService bedService, ComplainRepository complaintRepository) {
        this.chartService = chartService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.bedService = bedService;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("fpt-dorm/admin/home")
    public String admin_homepage(Model model, Authentication authentication,
                                 @RequestParam(required = false, defaultValue = "0") int year,
                                 @RequestParam(required = false, defaultValue = "0") int month) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        List<Object[]> months = chartService.findDistinctMonths();
        model.addAttribute("months", months);

        // Lấy các năm duy nhất
        Set<Integer> uniqueYears = months.stream()
                .map(monthArr -> (Integer) monthArr[1])
                .collect(Collectors.toSet());
        model.addAttribute("years", uniqueYears);

        // Lấy tháng và năm hiện tại
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        // Nếu không có giá trị year và month từ request, sử dụng tháng và năm hiện tại
        if (year == 0) {
            year = currentYear;
        }
        if (month == 0) {
            month = currentMonth;
        }

        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        List<UsageService> usageServices = usageServiceService.getUsageServicesByMonthAndYear(month, year);
        if (year > 0 && month > 0) {
            List<Booking> bookings = chartService.findBookingsByMonth(year, month);
            Map<String, Long> statusCounts = chartService.getBookingStatusCountsForMonth(year, month);
            model.addAttribute("bookings", bookings);
            model.addAttribute("statusCounts", statusCounts);
        }
        Float totalElectricity = usageServices.stream()
                .map(UsageService::getElectricity)
                .reduce(Float::sum)
                .orElse(0.0f);
        Float totalWater = usageServices.stream()
                .map(UsageService::getWater)
                .reduce(Float::sum)
                .orElse(0.0f);
        Float totalOthers = usageServices.stream()
                .map(UsageService::getOthers)
                .reduce(Float::sum)
                .orElse(0.0f);
        model.addAttribute("totalElectricity", totalElectricity);
        model.addAttribute("totalWater", totalWater);
        model.addAttribute("totalOthers", totalOthers);


        List<Map<String, Long>> statusComCounts = complaintRepository.countByStatusForMonth(year, month);

        // Add data to model for view rendering
        model.addAttribute("statusComCounts", statusComCounts);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMonth", month);

        return "admin/admin_homepage";
    }

    @GetMapping("/active-users")
    public ResponseEntity<Long> getActiveUserCount() {
        long count = userService.getActiveUserCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-used-beds")
    public ResponseEntity<Long> countUsedBeds() {
        long usedBedsCount = bedService.countUsedBeds();
        return ResponseEntity.ok(usedBedsCount);
    }

    @GetMapping("/count-new-users")
    public ResponseEntity<Integer> countNewUsers() {
        Role role = Role.USER; // Assuming USER role
        int count = userService.countNewUsersInCurrentMonth(role);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-complaints")
    public ResponseEntity<Integer> countComplaints() {
        int count = complainService.countComplaintsInCurrentMonth();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-employees")
    public ResponseEntity<Integer> countNewEmployees() {
        int count = userService.countNewEmployeesInCurrentMonth();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-bookings")
    public ResponseEntity<Integer> countBookings() {
        int count = bookingService.countBookingsInCurrentMonth();
        return ResponseEntity.ok(count);
    }

    @GetMapping("fpt-dorm/admin/admin_list_student")
    public String admin_list_students() {
        return "redirect:/fpt-dorm/admin/student/all-student";
    }

    @GetMapping("fpt-dorm/admin/admin_list_employees")
    public String admin_list_employees() {
        return "redirect:/fpt-dorm/admin/employee/all-employee";
    }

    @GetMapping("fpt-dorm/admin/admin_add_student")
    public String admin_add_student() {
        return "redirect:/fpt-dorm/admin/student/add";
    }

    @GetMapping("fpt-dorm/admin/admin_add_employee")
    public String admin_add_employee() {
        return "redirect:/fpt-dorm/admin/employee/add";
    }

    @GetMapping("fpt-dorm/admin/admin_list_room")
    public String admin_list_room() {
        return "admin_list_room";
    }

    @GetMapping("fpt-dorm/admin/admin_payment_list")
    public String admin_payment_list() {
        return "redirect:/fpt-dorm/admin/all-payment";
    }

    @GetMapping("fpt-dorm/admin/admin_payment_request")
    public String admin_payment_request() {
        return "redirect:/fpt-dorm/admin/payment-request";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_type_room")
    public String admin_add_new_type_room() {
        return "admin_add_new_type_room";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_room")
    public String admin_add_new_room() {
        return "admin_add_new_room";
    }

    @GetMapping("fpt-dorm/admin/admin_list_feedback")
    public String admin_list_feedback() {
        return "admin_list_feedback";
    }

    @GetMapping("fpt-dorm/admin/admin_list_complaint")
    public String admin_complain(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "3") int size,
                                 @RequestParam(name = "status", required = false) Complaint.Status status, Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        try {
            Page<Complaint> complainList;
            if (status != null) {
                // Filter complainList based on status
                complainList = complainRepository.findDoneComplaints(status, pageable);
            } else {
                // If no status is selected, get all complaints
                complainList = complainRepository.findAll(pageable);
            }
            if (complainList.isEmpty()) {
                // Handle case where complainList is empty
                model.addAttribute("message", "No complaints found with the selected status.");
                // Optionally, you can redirect to another page or render different view
                // return "redirect:/someOtherPage";
            } else {
                model.addAttribute("complaintDTOList", complainList);
            }
            model.addAttribute("statusForm", status);
            return "admin/admin_list_complaint";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }

    @GetMapping("/fpt-dorm/admin/complain/execute/{id}")
    public String executeComplain(@PathVariable("id") int id, Model model) {
        var complain = complainRepository.getRequestByComplaintId(id);
        model.addAttribute("complainObj", complain);
        return "admin/execute_complain";
    }

    @PostMapping("/fpt-dorm/admin/complain/execute/{id}")
    public String executeComplain(Model model, @PathVariable("id") int id, @RequestParam("status") Complaint.Status status, @RequestParam("reply") String reply) {
        Complaint existComplaint = complainRepository.getRequestByComplaintId(id);
        if (existComplaint != null) {
            existComplaint.setStatus(status);
            existComplaint.setReply(reply);
            complainService.saveComplain(existComplaint);

            return "redirect:/fpt-dorm/admin/admin_list_complaint";
        } else {
            return "error/403";
        }
    }


    @GetMapping("/fpt-dorm/admin/admin_Resident_History")
    public String admin_list_residentH() {
        return "redirect:/fpt-dorm/admin/Resident_History/list";
    }

    @GetMapping("/fpt-dorm/admin/usage-service")
    public String showListUsageService(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        List<Dorm> dorms = dormRepository.findAll();
        model.addAttribute("dorms", dorms);
        return "admin/admin_usageService";
    }

    @PostMapping("/fpt-dorm/admin/usage-service/{id}")
    public ResponseEntity<String> executeUsageServiceData(@PathVariable(name = "id") int id,
                                                          @RequestParam("electric") int electric,
                                                          @RequestParam("water") int water,
                                                          @RequestParam("others") int others,
                                                          Authentication authentication) {
        List<UserBookingDTO> usageServiceDTOs = userBookingRepository.findCurrentBookingsByRoomId(id);

        if (usageServiceDTOs.isEmpty()) {
            // Return a 400 Bad Request status with an error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This room is currently unoccupied.");
        }

        // Calculate usage costs
        float e = (electric * 4000f) / usageServiceDTOs.size();
        float w = (water * 5000f) / usageServiceDTOs.size();
        float o = (others * 1000f) / usageServiceDTOs.size();

        // Save usage services for each booking
        for (UserBookingDTO user : usageServiceDTOs) {
            UsageService usageService = new UsageService();
            usageService.setUser(userRepository.searchUserById(user.getUserId()));
            usageService.setBookingId(user.getBookingId());
            usageService.setElectricity(e);
            usageService.setWater(w);
            usageService.setOthers(o);
            usageServiceService.saveUsageService(usageService);
        }

        // Retrieve the email from authentication
        String email = null;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            email = userDetails.getUsername();
        }

        // Log the email for debugging purposes
        System.out.println("Processed by: " + (email != null ? email : "Unknown"));

        // Return success response
        return ResponseEntity.ok("Data submitted successfully.");
    }

    @GetMapping("/fpt-dorm/admin/admin_all_rooms")
    public String admin_list_all_rooms() {
        return "redirect:/fpt-dorm/admin/all-room";
    }
}
