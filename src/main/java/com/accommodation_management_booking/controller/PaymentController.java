package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.config.PaypalPaymentIntent;
import com.accommodation_management_booking.config.PaypalPaymentMethod;
import com.accommodation_management_booking.config.VNPayConfig;
import com.accommodation_management_booking.dto.PaymentTransactionDTO;
import com.accommodation_management_booking.entity.*;
import com.accommodation_management_booking.repository.*;
import com.accommodation_management_booking.service.BedService;
import com.accommodation_management_booking.service.EmailService;
import com.accommodation_management_booking.service.PaymentService;
import com.accommodation_management_booking.service.PaypalService;
//import com.accommodation_management_booking.service.VNPayService;
import com.accommodation_management_booking.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@AllArgsConstructor
public class PaymentController {

    private final BookingRepository bookingRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final ContractRepository contractRepository;
    private final BedService bedService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaypalService paypalService;
    private final BedRepository bedRepository;


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
        List<String> bookingSortFields = List.of("totalPrice");
        Page<PaymentTransactionDTO> paymentPage = paymentService.searchAllPayment(userId, keyword, category, bookingSortFields, pageable);
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
                model.addAttribute("user", userRepository.findByEmail(paymentTransactionDTO.getEmail()));
                model.addAttribute("contract", contractRepository.getContractByBookingId(paymentTransactionDTO.getBookingId()));
                Bed b = bedService.getBedById(paymentTransactionDTO.getBedId());
                model.addAttribute("bedName", b.getBedName());
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
        List<String> bookingSortFields = List.of("totalPrice");
        Page<PaymentTransactionDTO> paymentPage = paymentService.searchPaymentRequest(keyword, category, bookingSortFields, pageable);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "employee/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/employee/payment-request/id={id}")
    public String showPaymentRequestDetailEmployee(Model model, @PathVariable("id") int id) {
        try {
            PaymentTransactionDTO paymentTransactionDTO = paymentService.findByPaymentId(id);
            if (paymentTransactionDTO != null) {
                model.addAttribute("payment", paymentTransactionDTO);
                model.addAttribute("user", userRepository.findByEmail(paymentTransactionDTO.getEmail()));
                Bed b = bedService.getBedById(paymentTransactionDTO.getBedId());
                model.addAttribute("bedName", b.getBedName());
            } else {
                throw new Exception("Payment not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/employee/all-payment";
        }
        return "employee/payment/payment_request_detail";
    }

//    @GetMapping("/fpt-dorm/employee/payment-request/cancel/id={id}")
//    public ResponseEntity<String> cancelBooking(@PathVariable("id") int id) {
//        Optional<Booking> optionalBooking = bookingRepository.findById(id);
//
//        if (optionalBooking.isPresent()) {
//            Booking booking = optionalBooking.get();
//            booking.setStatus(Booking.Status.Canceled);
//            bookingRepository.save(booking);
//            return ResponseEntity.ok("Canceled successfully");
//        } else {
//            return ResponseEntity.status(404).body("Booking not found");
//        }
//    }

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

            String encodedBookingId = encode(booking.getBookingId());

            // Gửi email
            String toEmail = booking.getUser().getEmail(); // Email người dùng
            String subject = "Payment-Confirmed"; // Tiêu đề email
            String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                    "Your payment was confirmed." +
                    "\nPlease access this link to sign your contract: " + "http://localhost:8080/fpt-dorm/signature?token=" + encodedBookingId +
                    "\n\nThank you for your booking."; // Nội dung email

            emailService.sendBill(toEmail, subject, body);

            return ResponseEntity.ok("Payment confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
    }

    //User
    @GetMapping("/fpt-dorm/user/payment")
    public String showPaymentUser(Model model, Authentication authentication, HttpSession session,
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
        model.addAttribute("role", session.getAttribute("role"));
        return "user/payment";
    }

    @GetMapping("/fpt-dorm/user/payment/id={id}")
    public String showPaymentDetailUser(Model model, Authentication authentication, HttpSession session, @PathVariable("id") int id) {
        model.addAttribute("role", session.getAttribute("role"));
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
                Bed b = bedService.getBedById(paymentTransactionDTO.getBedId());
                model.addAttribute("bedName", b.getBedName());
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            paymentPage = paymentService.searchByUserWithBookingSort(id, pageable);
        } else {
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
        List<String> bookingSortFields = List.of("totalPrice");
        Page<PaymentTransactionDTO> paymentPage = paymentService.searchAllPayment(userId, keyword, category, bookingSortFields, pageable);
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
                model.addAttribute("user", userRepository.findByEmail(paymentTransactionDTO.getEmail()));
                model.addAttribute("contract", contractRepository.getContractByBookingId(paymentTransactionDTO.getBookingId()));
                Bed b = bedService.getBedById(paymentTransactionDTO.getBedId());
                model.addAttribute("bedName", b.getBedName());
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        List<String> bookingSortFields = List.of("totalPrice");
        if (bookingSortFields.contains(sortParams[0])) {
            paymentPage = paymentService.searchByStatusWithBookingSort(Booking.Status.Pending, pageable);
        } else {
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
        List<String> bookingSortFields = List.of("totalPrice");
        Page<PaymentTransactionDTO> paymentPage = paymentService.searchPaymentRequest(keyword, category, bookingSortFields, pageable);
        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "admin/payment/payment_request";
    }

    @GetMapping("/fpt-dorm/admin/payment-request/id={id}")
    public String showPaymentRequestDetailAdmin(Model model, @PathVariable("id") int id) {
        try {
            PaymentTransactionDTO paymentTransactionDTO = paymentService.findByPaymentId(id);
            if (paymentTransactionDTO != null) {
                model.addAttribute("payment", paymentTransactionDTO);
                model.addAttribute("user", userRepository.findByEmail(paymentTransactionDTO.getEmail()));
                Bed b = bedService.getBedById(paymentTransactionDTO.getBedId());
                model.addAttribute("bedName", b.getBedName());
            } else {
                throw new Exception("Payment not found");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/admin/all-payment";
        }
        return "admin/payment/payment_request_detail";
    }

    @GetMapping("/fpt-dorm/admin/payment-request/cancel/id={id}")
    public ResponseEntity<String> cancelBookingAdmin(@PathVariable("id") int id) throws IOException {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            System.out.println("Booking found: " + booking);
            booking.setStatus(Booking.Status.Canceled);
            bookingRepository.save(booking);

            if (booking.getAmountPaid() > 0) {
                Optional<com.accommodation_management_booking.entity.Payment> optionalPayment = paymentRepository.findByBooking(booking);
                if (optionalPayment.isPresent()) {
                    if (optionalPayment.get().getPaymentMethod() == com.accommodation_management_booking.entity.Payment.PaymentMethod.PayPal) {
                        System.out.println("Paypal");
                        com.accommodation_management_booking.entity.Payment payment = optionalPayment.get();
                        System.out.println("Payment found: " + payment);
                        try {
                            // Get the current exchange rate from VND to USD
                            float exchangeRate = getExchangeRateVNDToUSD();
                            if (exchangeRate == 0) {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve exchange rate.");
                            }

                            // Convert the amount paid from VND to USD
                            float amountPaidInUSD = booking.getAmountPaid() * exchangeRate - 3;
                            float refundAmount = amountPaidInUSD / exchangeRate;
                            // Logging for debugging
                            System.out.println("Amount paid in VND: " + booking.getAmountPaid());
                            System.out.println("Exchange rate VND to USD: " + exchangeRate);
                            System.out.println("Amount to be refunded in USD: " + amountPaidInUSD);
                            paypalService.refundPayment(payment.getPaymentDetail(), amountPaidInUSD);
                            booking.setRefundAmount(refundAmount);
                            booking.setRefundDate(LocalDate.now());
                            bookingRepository.save(booking);


                            // Update the bed status
                            Integer bedId = booking.getBed().getBedId();
                            Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
                            bed.setIsAvailable(true);
                            bedRepository.save(bed);

                            // Send email
                            String toEmail = booking.getUser().getEmail(); // Assuming you have a getEmail method in your Customer entity
                            String subject = "Refund successful";
                            String body = "Dear " + booking.getUser().getUsername() + ",\n\nYour payment has been refunded successfully" +
                                    "\n Please check your account: " + payment.getPaymentDetail() +
                                    "\nRefundAmount: " + booking.getRefundAmount() +
                                    "\nDate: " + booking.getRefundDate() +
                                    "\nDue to PayPal's refund policy, your refund may vary from the amount you paid!" +
                                    "\nAny questions or complaints please contact us:" +
                                    "\nAddress: Education and Training Area - Hoa Lac High-Tech Park - Km29 Thang Long Avenue, Thach That, City. HN" +
                                    "\nPhone: (024) 7300.1866 / (024) 7300.5588" +
                                    "\n\nThank you for using our service.";
                            emailService.sendBill(toEmail, subject, body);

                            return ResponseEntity.ok("Canceled successfully");
                        } catch (PayPalRESTException e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Refund failed: " + e.getMessage());
                        }
                    } else if (optionalPayment.get().getPaymentMethod() == com.accommodation_management_booking.entity.Payment.PaymentMethod.BankQRCode) {
                        System.out.println("VNPay");
                        com.accommodation_management_booking.entity.Payment payment = optionalPayment.get();
                        System.out.println("Payment found: " + payment);
                        //Command: refund
                        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
                        String vnp_Version = "2.1.0";
                        String vnp_Command = "refund";
                        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
                        String vnp_TransactionType = "refund";
                        String vnp_TxnRef = payment.getPaymentDetail();
                        long amount = Math.round(booking.getAmountPaid());
                        String vnp_Amount = String.valueOf(amount);
                        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
                        String vnp_TransactionNo = payment.getPaymentDetail();
                        String vnp_TransactionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String vnp_CreateBy = "admin";

                        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        String vnp_CreateDate = formatter.format(cld.getTime());

                        String vnp_IpAddr = "127.0.0.1";

                        JsonObject vnp_Params = new JsonObject();

                        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
                        vnp_Params.addProperty("vnp_Version", vnp_Version);
                        vnp_Params.addProperty("vnp_Command", vnp_Command);
                        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
                        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
                        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
                        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
                        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

                        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);

                        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
                        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
                        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
                        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

                        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

                        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data.toString());

                        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

                        // Log each parameter
                        System.out.println("vnp_RequestId: " + vnp_RequestId);
                        System.out.println("vnp_Version: " + vnp_Version);
                        System.out.println("vnp_Command: " + vnp_Command);
                        System.out.println("vnp_TmnCode: " + vnp_TmnCode);
                        System.out.println("vnp_TransactionType: " + vnp_TransactionType);
                        System.out.println("vnp_TxnRef: " + vnp_TxnRef);
                        System.out.println("vnp_Amount: " + vnp_Amount);
                        System.out.println("vnp_OrderInfo: " + vnp_OrderInfo);
                        System.out.println("vnp_TransactionDate: " + vnp_TransactionDate);
                        System.out.println("vnp_CreateBy: " + vnp_CreateBy);
                        System.out.println("vnp_CreateDate: " + vnp_CreateDate);
                        System.out.println("vnp_IpAddr: " + vnp_IpAddr);
                        System.out.println("vnp_SecureHash: " + vnp_SecureHash);

                        URL url = new URL(VNPayConfig.vnp_ApiUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(vnp_Params.toString());
                        wr.flush();
                        wr.close();
                        int responseCode = con.getResponseCode();
                        System.out.println("nSending 'POST' request to URL : " + url);
                        System.out.println("Post Data : " + vnp_Params);
                        System.out.println("Response Code : " + responseCode);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String output;
                        StringBuffer response = new StringBuffer();
                        while ((output = in.readLine()) != null) {
                            response.append(output);
                        }
                        in.close();
                        System.out.println(response.toString());

                        booking.setRefundAmount(Float.parseFloat(vnp_Amount));
                        booking.setRefundDate(LocalDate.now());
                        bookingRepository.save(booking);

                        // Update the bed status
                        Integer bedId = booking.getBed().getBedId();
                        Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
                        bed.setIsAvailable(true);
                        bedRepository.save(bed);

                        // Send email
                        String toEmail = booking.getUser().getEmail(); // Assuming you have a getEmail method in your Customer entity
                        String subject = "Refund successful";
                        String body = "Dear " + booking.getUser().getUsername() + ",\n\nYour payment has been refunded successfully" +
                                "\n Please check your account: " + payment.getPaymentDetail() +
                                "\nRefundAmount: " + booking.getRefundAmount() +
                                "\nDate: " + booking.getRefundDate() +
                                "\nDue to VNPay's refund policy, your refund may vary from the amount you paid!" +
                                "\nAny questions or complaints please contact us:" +
                                "\nAddress: Education and Training Area - Hoa Lac High-Tech Park - Km29 Thang Long Avenue, Thach That, City. HN" +
                                "\nPhone: (024) 7300.1866 / (024) 7300.5588" +
                                "\n\nThank you for using our service.";
                        emailService.sendBill(toEmail, subject, body);

                        return ResponseEntity.ok("Canceled successfully");
                    }

                }
            }else {
                System.out.println("No payment found for this booking");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No payment found for this booking");
            }
            return ResponseEntity.ok("Canceled successfully, no payment to refund");
        }else {
            System.out.println("Booking not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
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

            String encodedBookingId = encode(booking.getBookingId());

            // Gửi email
            String toEmail = booking.getUser().getEmail(); // Email người dùng
            String subject = "Payment-Confirmed"; // Tiêu đề email
            String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                    "Your payment was confirmed." +
                    "\nPlease access this link to sign your contract: " + "http://localhost:8080/fpt-dorm/signature?token=" + encodedBookingId +
                    "\n\nThank you for your booking."; // Nội dung email

            emailService.sendBill(toEmail, subject, body);

            return ResponseEntity.ok("Payment confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
    }

    public static String encode(int bookingId) {
        String idAsString = String.valueOf(bookingId);
        return Base64.getUrlEncoder().encodeToString(idAsString.getBytes());
    }


    @GetMapping("/fpt-dorm/employee/payment-request/cancel/id={id}")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") int id) throws IOException {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            System.out.println("Booking found: " + booking);
            booking.setStatus(Booking.Status.Canceled);
            bookingRepository.save(booking);

            if (booking.getAmountPaid() > 0) {
                Optional<com.accommodation_management_booking.entity.Payment> optionalPayment = paymentRepository.findByBooking(booking);
                if (optionalPayment.isPresent()) {
                    if (optionalPayment.get().getPaymentMethod() == com.accommodation_management_booking.entity.Payment.PaymentMethod.PayPal) {
                        System.out.println("Paypal");
                        com.accommodation_management_booking.entity.Payment payment = optionalPayment.get();
                        System.out.println("Payment found: " + payment);
                        try {
                            // Get the current exchange rate from VND to USD
                            float exchangeRate = getExchangeRateVNDToUSD();
                            if (exchangeRate == 0) {
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve exchange rate.");
                            }

                            // Convert the amount paid from VND to USD
                            float amountPaidInUSD = booking.getAmountPaid() * exchangeRate-3;
                            float refundAmount = amountPaidInUSD / exchangeRate;
                            // Logging for debugging
                            System.out.println("Amount paid in VND: " + booking.getAmountPaid());
                            System.out.println("Exchange rate VND to USD: " + exchangeRate);
                            System.out.println("Amount to be refunded in USD: " + amountPaidInUSD);
                            paypalService.refundPayment(payment.getPaymentDetail(), amountPaidInUSD);
                            booking.setRefundAmount(refundAmount);
                            booking.setRefundDate(LocalDate.now());
                            bookingRepository.save(booking);


                            // Update the bed status
                            Integer bedId = booking.getBed().getBedId();
                            Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
                            bed.setIsAvailable(true);
                            bedRepository.save(bed);

                            // Send email
                            String toEmail = booking.getUser().getEmail(); // Assuming you have a getEmail method in your Customer entity
                            String subject = "Refund successful";
                            String body = "Dear " + booking.getUser().getUsername() + ",\n\nYour payment has been refunded successfully" +
                                    "\n Please check your account: " + payment.getPaymentDetail() +
                                    "\nRefundAmount: " + booking.getRefundAmount() +
                                    "\nDate: " + booking.getRefundDate() +
                                    "\nDue to PayPal's refund policy, your refund may vary from the amount you paid!" +
                                    "\nAny questions or complaints please contact us:" +
                                    "\nAddress: Education and Training Area - Hoa Lac High-Tech Park - Km29 Thang Long Avenue, Thach That, City. HN" +
                                    "\nPhone: (024) 7300.1866 / (024) 7300.5588" +
                                    "\n\nThank you for using our service.";
                            emailService.sendBill(toEmail, subject, body);

                            return ResponseEntity.ok("Canceled successfully");
                        } catch (PayPalRESTException e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Refund failed: " + e.getMessage());
                        }
                    } else if (optionalPayment.get().getPaymentMethod() == com.accommodation_management_booking.entity.Payment.PaymentMethod.BankQRCode) {
                        System.out.println("VNPay");
                        com.accommodation_management_booking.entity.Payment payment = optionalPayment.get();
                        System.out.println("Payment found: " + payment);
                        //Command: refund
                        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
                        String vnp_Version = "2.1.0";
                        String vnp_Command = "refund";
                        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
                        String vnp_TransactionType = "refund";
                        String vnp_TxnRef = payment.getPaymentDetail();
                        long amount = Math.round(booking.getAmountPaid());
                        String vnp_Amount = String.valueOf(amount);
                        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
                        String vnp_TransactionNo = payment.getPaymentDetail();
                        String vnp_TransactionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String vnp_CreateBy = "admin";

                        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        String vnp_CreateDate = formatter.format(cld.getTime());

                        String vnp_IpAddr = "127.0.0.1";

                        JsonObject vnp_Params = new JsonObject();

                        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
                        vnp_Params.addProperty("vnp_Version", vnp_Version);
                        vnp_Params.addProperty("vnp_Command", vnp_Command);
                        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
                        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
                        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
                        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
                        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

                        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);

                        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
                        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
                        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
                        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

                        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                                vnp_TransactionType, vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

                        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data.toString());

                        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

                        // Log each parameter
                        System.out.println("vnp_RequestId: " + vnp_RequestId);
                        System.out.println("vnp_Version: " + vnp_Version);
                        System.out.println("vnp_Command: " + vnp_Command);
                        System.out.println("vnp_TmnCode: " + vnp_TmnCode);
                        System.out.println("vnp_TransactionType: " + vnp_TransactionType);
                        System.out.println("vnp_TxnRef: " + vnp_TxnRef);
                        System.out.println("vnp_Amount: " + vnp_Amount);
                        System.out.println("vnp_OrderInfo: " + vnp_OrderInfo);
                        System.out.println("vnp_TransactionDate: " + vnp_TransactionDate);
                        System.out.println("vnp_CreateBy: " + vnp_CreateBy);
                        System.out.println("vnp_CreateDate: " + vnp_CreateDate);
                        System.out.println("vnp_IpAddr: " + vnp_IpAddr);
                        System.out.println("vnp_SecureHash: " + vnp_SecureHash);

                        URL url = new URL(VNPayConfig.vnp_ApiUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        wr.writeBytes(vnp_Params.toString());
                        wr.flush();
                        wr.close();
                        int responseCode = con.getResponseCode();
                        System.out.println("nSending 'POST' request to URL : " + url);
                        System.out.println("Post Data : " + vnp_Params);
                        System.out.println("Response Code : " + responseCode);
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String output;
                        StringBuffer response = new StringBuffer();
                        while ((output = in.readLine()) != null) {
                            response.append(output);
                        }
                        in.close();
                        System.out.println(response.toString());

                        booking.setRefundAmount(Float.parseFloat(vnp_Amount));
                        booking.setRefundDate(LocalDate.now());
                        bookingRepository.save(booking);

                        // Update the bed status
                        Integer bedId = booking.getBed().getBedId();
                        Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
                        bed.setIsAvailable(true);
                        bedRepository.save(bed);

                        // Send email
                        String toEmail = booking.getUser().getEmail(); // Assuming you have a getEmail method in your Customer entity
                        String subject = "Refund successful";
                        String body = "Dear " + booking.getUser().getUsername() + ",\n\nYour payment has been refunded successfully" +
                                "\n Please check your account: " + payment.getPaymentDetail() +
                                "\nRefundAmount: " + booking.getRefundAmount() +
                                "\nDate: " + booking.getRefundDate() +
                                "\nDue to VNPay's refund policy, your refund may vary from the amount you paid!" +
                                "\nAny questions or complaints please contact us:" +
                                "\nAddress: Education and Training Area - Hoa Lac High-Tech Park - Km29 Thang Long Avenue, Thach That, City. HN" +
                                "\nPhone: (024) 7300.1866 / (024) 7300.5588" +
                                "\n\nThank you for using our service.";
                        emailService.sendBill(toEmail, subject, body);

                        return ResponseEntity.ok("Canceled successfully");
                    }
                }
            }else {
                System.out.println("No payment found for this booking");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No payment found for this booking");
            }
            return ResponseEntity.ok("Canceled successfully, no payment to refund");
        }else {
            System.out.println("Booking not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

    }

    private float getExchangeRateVNDToUSD() {
        // Example implementation using a hypothetical external service
        // Replace with actual API call to get the current exchange rate
        try {
            // Example: Using a service like ExchangeRate-API
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangerate-api.com/v4/latest/VND";
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("rates")) {
                Map<String, Double> rates = (Map<String, Double>) responseBody.get("rates");
                return rates.get("USD").floatValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @GetMapping("/payments")
    @ResponseBody
    public List<com.accommodation_management_booking.entity.Payment> getPayments(@RequestParam com.accommodation_management_booking.entity.Payment.PaymentMethod method) {
        System.out.println("method: " + method);
        return paypalService.getPaymentsByMethod(method);
    }

}
