package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.PaymentTransactionDTO;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PaymentController {

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;

    @GetMapping("/fpt-dorm/employee/all-payment")
    public String showPaymentList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage = paymentService.getPayments(pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("sort", sort);
        return "employee/payment/all_payment";
    }

    @GetMapping("/fpt-dorm/employee/all-payment/search")
    public String searchPaymentList(Model model,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "category", required = false) String category,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage;

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                userPage = paymentService.getPayments(pageable);
            } else {
                userPage = paymentService.searchAll(keyword, pageable);
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int id = Integer.parseInt(keyword);
                        userPage = paymentService.searchByUser(id, pageable);
                    } catch (NumberFormatException e) {
                        userPage = Page.empty(pageable);
                    }
                    break;
                case "Name":
                    userPage = paymentService.searchByName(keyword, pageable);
                    break;
                case "Email":
                    userPage = paymentService.searchByMail(keyword, pageable);
                    break;
                case "Phone":
                    userPage = paymentService.searchByPhone(keyword, pageable);
                    break;
                default:
                    userPage = Page.empty(pageable);
                    break;
            }
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "employee/payment/all_payment";
    }

    @GetMapping("/fpt-dorm/employee/all-payment/student/id={id}")
    public String showStudentPaymentList(Model model,
                                         @PathVariable("id") int id,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,
                                         @RequestParam(defaultValue = "paymentId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<PaymentTransactionDTO> paymentPage;
        Pageable pageable;
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByUserWithBookingSort(id, pageable);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByUserWithPaymentSort(id, pageable);
        }
        model.addAttribute("userId", id);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("sort", sort);
        return "employee/payment/student_payment_list";
    }

    @GetMapping("/fpt-dorm/employee/all-payment/student/search")
    public String searchStudentPaymentListEmployee(Model model,
                                                   @RequestParam(value = "userId", required = false) int userId,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "category", required = false) String category,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size,
                                                   @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PaymentTransactionDTO> paymentPage;

        System.out.println("paymentDate: " + keyword);

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                paymentPage = Page.empty(pageable);
            } else {
                paymentPage = paymentService.searchByUserWithPaymentSort(userId, pageable);
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (paymentService.findByPaymentId(paymentId) != null) {
                            paymentPage = paymentService.findByPaymentIdWithPage(paymentId, pageable);
                        } else {
                            paymentPage = Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                case "Date":
                    try {
                        List<DateTimeFormatter> formatters = Arrays.asList(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        );
                        LocalDate paymentDate = null;
                        for (DateTimeFormatter formatter : formatters) {
                            try {
                                paymentDate = LocalDate.parse(keyword, formatter);
                                break;
                            } catch (DateTimeParseException _) {
                            }
                        }
                        if (paymentDate == null) {
                            paymentDate = LocalDate.parse(keyword);
                        }
                        paymentPage = paymentService.findByPaymentDateWithPage(paymentDate, userId, pageable);
                    } catch (DateTimeParseException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                default:
                    paymentPage = Page.empty(pageable);
                    break;
            }
        }
        model.addAttribute("userId", userId);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "employee/payment/student_payment_list";
    }

    @GetMapping("/fpt-dorm/employee/all-payment/student/payment/id={id}")
    public String showPaymentDetail(Model model, @PathVariable("id") int id) {
        try {
            PaymentTransactionDTO paymentTransactionDTO = paymentService.findByPaymentId(id);
            if (paymentTransactionDTO != null) {
                model.addAttribute("payment", paymentTransactionDTO);
            } else {
                throw new Exception("Payment not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/employee/all-payment";
        }
        return "employee/payment/payment_detail";
    }

    @GetMapping("/fpt-dorm/employee/payment-request")
    public String showPaymentRequestList(Model model,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,
                                         @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<PaymentTransactionDTO> paymentPage;
        Pageable pageable;
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByStatusWithBookingSort(Booking.Status.Pending, pageable);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByStatusWithPaymentSort(Booking.Status.Pending, pageable);
        }
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("sort", sort);
        return "employee/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/employee/payment-request/search")
    public String searchStudentPaymentRequestEmployee(Model model,
                                                      @RequestParam(value = "keyword", required = false) String keyword,
                                                      @RequestParam(value = "category", required = false) String category,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size,
                                                      @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PaymentTransactionDTO> paymentPage;

        System.out.println("paymentDate: " + keyword);

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                paymentPage = Page.empty(pageable);
            } else {
                model.addAttribute("paymentPage", Page.empty(pageable));
                model.addAttribute("keyword", keyword);
                model.addAttribute("selectedCategory", category);
                model.addAttribute("sort", sort);
                return "admin/payment/payment_request";
//                return "redirect:/fpt-dorm/admin/payment-request";
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (paymentService.findByPaymentId(paymentId) != null) {
                            paymentPage = paymentService.findPaymentRequestByPaymentId(paymentId, pageable);
                        } else {
                            paymentPage = Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                case "Date":
                    try {
                        List<DateTimeFormatter> formatters = Arrays.asList(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        );
                        LocalDate paymentDate = null;
                        for (DateTimeFormatter formatter : formatters) {
                            try {
                                paymentDate = LocalDate.parse(keyword, formatter);
                                break;
                            } catch (DateTimeParseException ex) {
                            }
                        }
                        if (paymentDate == null) {
                            paymentDate = LocalDate.parse(keyword);
                        }
                        paymentPage = paymentService.findPaymentRequestByPaymentDate(paymentDate, pageable);
                    } catch (DateTimeParseException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                default:
                    paymentPage = Page.empty(pageable);
                    break;
            }
        }
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "admin/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/employee/payment-request/cancel/id={id}")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") int id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(Booking.Status.Canceled);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Canceled successfully");
        } else {
            return ResponseEntity.status(404).body("Booking not found");
        }
    }

    @PostMapping("/fpt-dorm/employee/payment-request/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody Booking request) {
        int bookingId = request.getBookingId();
        float refundAmount = request.getRefundAmount();
        LocalDate refundDate = request.getRefundDate();

        // Simulate processing (replace with actual logic)
        // Example: Update database, send notifications, etc.
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Refund Amount: " + refundAmount);
        System.out.println("Refund Date: " + refundDate);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setRefundAmount(refundAmount);
            booking.setRefundDate(refundDate);
            booking.setStatus(Booking.Status.Confirmed);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Payment confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
    }

    //User
    @GetMapping("/fpt-dorm/user/payment")
    public String showPaymentUser(Model model, Authentication authentication,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "3") int size,
                                  @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String email;
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            model.addAttribute("email", "Unknown");
            return "user/payment";
        }
        if (email != null) {
            String[] sortParams = sort.split(",");
            Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            Pageable pendingPageable = PageRequest.of(0, 20, Sort.by(direction, sortParams[0]));

            Page<PaymentTransactionDTO> pendingPaymentPage = paymentService.findPendingPaymentsByUserEmail(email, pendingPageable);
            Page<PaymentTransactionDTO> paymentPage = paymentService.findPaymentsByUserEmail(email, pageable);

            model.addAttribute("pendingPaymentPage", pendingPaymentPage);
            model.addAttribute("paymentPage", paymentPage);
            model.addAttribute("sort", sort);
            model.addAttribute("email", email);
        }

        return "user/payment";
    }

    @GetMapping("/fpt-dorm/user/payment/id={id}")
    public String showPaymentDetailUser(Model model, Authentication authentication, @PathVariable("id") int id) {
        String email;
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else {
            model.addAttribute("email", "Unknown");
            return "user/payment";
        }
        try {
            PaymentTransactionDTO paymentTransactionDTO = paymentService.findByPaymentId(id);
            if (paymentTransactionDTO != null && paymentTransactionDTO.getEmail().equals(email)) {
                model.addAttribute("payment", paymentTransactionDTO);
            } else {
                throw new Exception("Payment not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/user/payment";
        }
        return "user/student_payment_detail";
    }

    //Admin
    @GetMapping("/fpt-dorm/admin/all-payment")
    public String showPaymentListAdmin(Model model,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage = paymentService.getPayments(pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("sort", sort);
        return "admin/payment/all_payment";
    }

    @GetMapping("/fpt-dorm/admin/all-payment/search")
    public String searchPaymentListAdmin(Model model,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "category", required = false) String category,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage;

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                userPage = paymentService.getPayments(pageable);
            } else {
                userPage = paymentService.searchAll(keyword, pageable);
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int id = Integer.parseInt(keyword);
                        userPage = paymentService.searchByUser(id, pageable);
                    } catch (NumberFormatException e) {
                        userPage = Page.empty(pageable);
                    }
                    break;
                case "Name":
                    userPage = paymentService.searchByName(keyword, pageable);
                    break;
                case "Email":
                    userPage = paymentService.searchByMail(keyword, pageable);
                    break;
                case "Phone":
                    userPage = paymentService.searchByPhone(keyword, pageable);
                    break;
                default:
                    userPage = Page.empty(pageable);
                    break;
            }
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "admin/payment/all_payment";
    }

    @GetMapping("/fpt-dorm/admin/all-payment/student/id={id}")
    public String showStudentPaymentListAdmin(Model model,
                                         @PathVariable("id") int id,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,
                                         @RequestParam(defaultValue = "paymentId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<PaymentTransactionDTO> paymentPage;
        Pageable pageable;
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByUserWithBookingSort(id, pageable);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByUserWithPaymentSort(id, pageable);
        }
        model.addAttribute("userId", id);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("sort", sort);
        return "admin/payment/student_payment_list";
    }

    @GetMapping("/fpt-dorm/admin/all-payment/student/search")
    public String searchStudentPaymentListAdmin(Model model,
                                           @RequestParam(value = "userId", required = false) int userId,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "category", required = false) String category,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size,
                                           @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PaymentTransactionDTO> paymentPage;

        System.out.println("paymentDate: " + keyword);

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                paymentPage = Page.empty(pageable);
            } else {
                paymentPage = paymentService.searchByUserWithPaymentSort(userId, pageable);
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (paymentService.findByPaymentId(paymentId) != null) {
                            paymentPage = paymentService.findByPaymentIdWithPage(paymentId, pageable);
                        } else {
                            paymentPage = Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                case "Date":
                    try {
                        List<DateTimeFormatter> formatters = Arrays.asList(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        );
                        LocalDate paymentDate = null;
                        for (DateTimeFormatter formatter : formatters) {
                            try {
                                paymentDate = LocalDate.parse(keyword, formatter);
                                break;
                            } catch (DateTimeParseException _) {
                            }
                        }
                        if (paymentDate == null) {
                            paymentDate = LocalDate.parse(keyword);
                        }
                        paymentPage = paymentService.findByPaymentDateWithPage(paymentDate, userId, pageable);
                    } catch (DateTimeParseException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                default:
                    paymentPage = Page.empty(pageable);
                    break;
            }
        }
        model.addAttribute("userId", userId);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "admin/payment/student_payment_list";
    }

    @GetMapping("/fpt-dorm/admin/all-payment/student/payment/id={id}")
    public String showPaymentDetailAdmin(Model model, @PathVariable("id") int id) {
        try {
            PaymentTransactionDTO paymentTransactionDTO = paymentService.findByPaymentId(id);
            if (paymentTransactionDTO != null) {
                model.addAttribute("payment", paymentTransactionDTO);
            } else {
                throw new Exception("Payment not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/admin/all-payment";
        }
        return "admin/payment/payment_detail";
    }

    @GetMapping("/fpt-dorm/admin/payment-request")
    public String showPaymentRequestListAdmin(Model model,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,
                                         @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<PaymentTransactionDTO> paymentPage;
        Pageable pageable;
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByStatusWithBookingSort(Booking.Status.Pending, pageable);
        } else {
            pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
            paymentPage = paymentService.searchByStatusWithPaymentSort(Booking.Status.Pending, pageable);
        }
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("sort", sort);
        return "admin/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/admin/payment-request/search")
    public String searchStudentPaymentRequestAdmin(Model model,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "category", required = false) String category,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size,
                                                   @RequestParam(defaultValue = "paymentDate,desc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PaymentTransactionDTO> paymentPage;

        System.out.println("paymentDate: " + keyword);

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                paymentPage = Page.empty(pageable);
            } else {
                model.addAttribute("paymentPage", Page.empty(pageable));
                model.addAttribute("keyword", keyword);
                model.addAttribute("selectedCategory", category);
                model.addAttribute("sort", sort);
                return "admin/payment/payment_request";
//                return "redirect:/fpt-dorm/admin/payment-request";
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (paymentService.findByPaymentId(paymentId) != null) {
                            paymentPage = paymentService.findPaymentRequestByPaymentId(paymentId, pageable);
                        } else {
                            paymentPage = Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                case "Date":
                    try {
                        List<DateTimeFormatter> formatters = Arrays.asList(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        );
                        LocalDate paymentDate = null;
                        for (DateTimeFormatter formatter : formatters) {
                            try {
                                paymentDate = LocalDate.parse(keyword, formatter);
                                break;
                            } catch (DateTimeParseException ex) {
                            }
                        }
                        if (paymentDate == null) {
                            paymentDate = LocalDate.parse(keyword);
                        }
                        paymentPage = paymentService.findPaymentRequestByPaymentDate(paymentDate, pageable);
                    } catch (DateTimeParseException e) {
                        paymentPage = Page.empty(pageable);
                    }
                    break;
                default:
                    paymentPage = Page.empty(pageable);
                    break;
            }
        }
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "admin/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/admin/payment-request/cancel/id={id}")
    public ResponseEntity<String> cancelBookingAdmin(@PathVariable("id") int id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(Booking.Status.Canceled);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Canceled successfully");
        } else {
            return ResponseEntity.status(404).body("Booking not found");
        }
    }

    @PostMapping("/fpt-dorm/admin/payment-request/confirm")
    public ResponseEntity<String> confirmPaymentAdmin(@RequestBody Booking request) {
        int bookingId = request.getBookingId();
        float refundAmount = request.getRefundAmount();
        LocalDate refundDate = request.getRefundDate();

        // Simulate processing (replace with actual logic)
        // Example: Update database, send notifications, etc.
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Refund Amount: " + refundAmount);
        System.out.println("Refund Date: " + refundDate);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setRefundAmount(refundAmount);
            booking.setRefundDate(refundDate);
            booking.setStatus(Booking.Status.Confirmed);
            bookingRepository.save(booking);
            return ResponseEntity.ok("Payment confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
    }

}
