package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.config.PaypalPaymentIntent;
import com.accommodation_management_booking.config.PaypalPaymentMethod;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.*;
import com.accommodation_management_booking.service.EmailService;
import com.accommodation_management_booking.service.PaypalService;
import com.accommodation_management_booking.utils.Utils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class PayPalController {
    public static final String URL_PAYPAL_SUCCESS = "pay/success";
    public static final String URL_PAYPAL_CANCEL = "pay/cancel";

    //private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PaypalService paypalService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

//    @Autowired
//    private VNPayService vnPayService;

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

    @PostMapping("/fpt-dorm/user/booking/pay")
    public String pay(HttpServletRequest request,
                      @RequestParam("bed") Integer bedId,
                      @RequestParam("room") Integer roomId,
                      @RequestParam("checkin") LocalDate checkinDate,
                      @RequestParam("checkout") LocalDate checkoutDate,
                      @RequestParam("totalPrice") float totalPrice,
                      @RequestParam("totalPriceUSD") float price) {

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
//        booking.setAmountPaid(totalPrice);
        bookingRepository.save(booking);

//        bed.setIsAvailable(false);
//        bedRepository.save(bed);

        // Store booking ID in session
        request.getSession().setAttribute("bookingId", booking.getBookingId());
        request.getSession().setAttribute("bedId", bed.getBedId());

        String cancelUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_CANCEL;
        String successUrl = Utils.getBaseURL(request) + "/" + URL_PAYPAL_SUCCESS;
        try{
            Payment payment = paypalService.createPayment(
                    price,
                    "USD",
                    PaypalPaymentMethod.paypal,
                    PaypalPaymentIntent.sale,
                    "Payment for booking",
                    cancelUrl,
                    successUrl);
            for (Links link : ((com.paypal.api.payments.Payment) payment).getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping(URL_PAYPAL_CANCEL)
    public String cancelPay(HttpServletRequest request) {
        // Store booking ID in session
        Integer bookingId = (Integer) request.getSession().getAttribute("bookingId");
        bookingRepository.deleteById(bookingId);
        Integer bedId = (Integer) request.getSession().getAttribute("bedId");
        Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
        bed.setIsAvailable(true);
        bedRepository.save(bed);
        return "cancel";
    }

    @GetMapping(URL_PAYPAL_SUCCESS)
    public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             HttpServletRequest request) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                Integer bookingId = (Integer) request.getSession().getAttribute("bookingId");
                com.accommodation_management_booking.entity.Payment payment1 = new com.accommodation_management_booking.entity.Payment();
                payment1.setPaymentMethod(com.accommodation_management_booking.entity.Payment.PaymentMethod.PayPal);
                payment1.setPaymentDetail(paymentId);
                payment1.setPaymentDate(LocalDateTime.now());
                // Set booking ID in payment
                Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
                payment1.setBooking(booking);
                paymentRepository.save(payment1);

                Integer bedId = (Integer) request.getSession().getAttribute("bedId");
                Bed bed = bedRepository.findById(bedId).orElseThrow(() -> new IllegalArgumentException("Invalid bed ID"));
                bed.setIsAvailable(false);
                bedRepository.save(bed);
                booking.setAmountPaid(payment1.getBooking().getTotalPrice());
                bookingRepository.save(booking);


                // Send email
                String toEmail = booking.getUser().getEmail(); // Assuming you have a getEmail method in your Customer entity
                String subject = "Payment Successful - Booking Confirmation";
                String body = "Dear " + booking.getUser().getUsername() + ",\n\nYour payment was successful." +
                        "\n Payment code: " + payment1.getPaymentDetail() +
                        "\nTotal Price: " + booking.getTotalPrice() +
                        "\nAmount Paid: " + booking.getAmountPaid() +
                        "\nDate: " + payment1.getPaymentDate() +
//                        "\nPlease access this link to sign your contract: " + "http://localhost:8080/fpt-dorm/signature?bookingId=" + booking.getBookingId() +
                        "\n\nThank you for your booking.";
                emailService.sendBill(toEmail, subject, body);

                return "success";
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        bookingRepository.delete(bookingRepository.findById((Integer) request.getSession().getAttribute("bookingId")).get());
        return "redirect:/";
    }
}
