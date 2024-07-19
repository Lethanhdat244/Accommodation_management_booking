package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.PaymentTransactionDTO;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.PaymentRepository;
import com.accommodation_management_booking.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public Page<User> getPayments(Pageable pageable) {
        return paymentRepository.findUsersWithBooking(pageable);
    }

    public Page<User> searchAll(String keyword, Pageable pageable) {
        return paymentRepository.searchAllInUsersWithBooking(keyword, pageable);
    }

    public Page<User> searchByUser(int userId, Pageable pageable) {
        return paymentRepository.searchByUserId(userId, pageable);
    }

    public Page<User> searchByName(String keyword, Pageable pageable) {
        return paymentRepository.searchByUsername(keyword, pageable);
    }

    public Page<User> searchByMail(String keyword, Pageable pageable) {
        return paymentRepository.searchByEmail(keyword, pageable);
    }

    public Page<User> searchByPhone(String keyword, Pageable pageable) {
        return paymentRepository.searchByPhoneNumber(keyword, pageable);
    }

    public Page<PaymentTransactionDTO> searchByUserWithPaymentSort(int userId, Pageable pageable) {
        return paymentRepository.searchPaymentByUserId(userId, pageable);
    }

    public Page<PaymentTransactionDTO> searchByUserWithBookingSort(int userId, Pageable pageable) {
        return paymentRepository.searchBookingByUserId(userId, pageable);
    }

    public PaymentTransactionDTO findByPaymentId(int id) {
        return paymentRepository.findByPaymentId(id);
    }

    public Page<PaymentTransactionDTO> searchByStatusWithPaymentSort(Booking.Status status, Pageable pageable) {
        return paymentRepository.searchByStatusPS(status, pageable);
    }

    public Page<PaymentTransactionDTO> searchByStatusWithBookingSort(Booking.Status status, Pageable pageable) {
        return paymentRepository.searchByStatusBS(status, pageable);
    }

    public Page<PaymentTransactionDTO> findPendingPaymentsByUserEmail(String email, Pageable pageable) {
        return paymentRepository.findPendingPaymentsByUserEmail(email, pageable);
    }

    public Page<PaymentTransactionDTO> findPaymentsByUserEmail(String email, Pageable pageable) {
        return paymentRepository.findPaymentsByUserEmail(email, pageable);
    }

    public Page<PaymentTransactionDTO> findByPaymentIdWithPagePaymentSort(int paymentId, Pageable pageable) {
        return paymentRepository.findByPaymentIdWithPagingPS(paymentId, pageable);
    }

    public Page<PaymentTransactionDTO> findByPaymentIdWithPageBookingSort(int paymentId, Pageable pageable) {
        return paymentRepository.findByPaymentIdWithPagingBS(paymentId, pageable);
    }

    public Page<PaymentTransactionDTO> findByPaymentDateWithPagePaymentSort(LocalDate paymentDate, int userId, Pageable pageable) {
        return paymentRepository.findByPaymentDateWithPagingPS(paymentDate, userId, pageable);
    }

    public Page<PaymentTransactionDTO> findByPaymentDateWithPageBookingSort(LocalDate paymentDate, int userId, Pageable pageable) {
        return paymentRepository.findByPaymentDateWithPagingBS(paymentDate, userId, pageable);
    }

    public Page<PaymentTransactionDTO> searchByPaymentIdWithPaymentSort(int paymentId, Pageable pageable) {
        return paymentRepository.findPaymentRequestByPaymentIdWithPagePS(paymentId, pageable);
    }

    public Page<PaymentTransactionDTO> searchByPaymentIdWithBookingSort(int paymentId, Pageable pageable) {
        return paymentRepository.findPaymentRequestByPaymentIdWithPageBS(paymentId, pageable);
    }

    public Page<PaymentTransactionDTO> searchByPaymentDateWithPaymentSort(LocalDate paymentDate, Pageable pageable) {
        return paymentRepository.findPaymentRequestByPaymentDateWithPagePS(paymentDate, pageable);
    }

    public Page<PaymentTransactionDTO> searchByPaymentDateWithBookingSort(LocalDate paymentDate, Pageable pageable) {
        return paymentRepository.findPaymentRequestByPaymentDateWithPageBS(paymentDate, pageable);
    }

    @Override
    public Page<PaymentTransactionDTO> searchAllPayment(int userId, String keyword, String category, List<String> bookingSortFields, Pageable pageable) {
        String sortParam = pageable.getSort().iterator().next().getProperty();
        if (keyword == null || keyword.isEmpty() || category == null || category.isEmpty()) {
            return Page.empty(pageable);
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (findByPaymentId(paymentId) != null) {
                            if (bookingSortFields.contains(sortParam)) {
                                return findByPaymentIdWithPageBookingSort(paymentId, pageable);
                            } else {
                                return findByPaymentIdWithPagePaymentSort(paymentId, pageable);
                            }
                        } else {
                            return Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        return Page.empty(pageable);
                    }
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
                        if (bookingSortFields.contains(sortParam)) {
                            return findByPaymentDateWithPageBookingSort(paymentDate, userId, pageable);
                        } else {
                            return findByPaymentDateWithPagePaymentSort(paymentDate, userId, pageable);
                        }

                    } catch (DateTimeParseException e) {
                        return Page.empty(pageable);
                    }
                default:
                    return Page.empty(pageable);
            }
        }
    }

    @Override
    public Page<PaymentTransactionDTO> searchPaymentRequest(String keyword, String category, List<String> bookingSortFields, Pageable pageable) {
        String sortParam = pageable.getSort().iterator().next().getProperty();
        if (keyword == null || keyword.isEmpty() || category == null || category.isEmpty()) {
            return Page.empty(pageable);
        } else {
            switch (category) {
                case "ID":
                    try {
                        int paymentId = Integer.parseInt(keyword);
                        if (findByPaymentId(paymentId) != null) {
                            if (bookingSortFields.contains(sortParam)) {
                                return searchByPaymentIdWithBookingSort(paymentId, pageable);
                            } else {
                                return searchByPaymentIdWithPaymentSort(paymentId, pageable);
                            }
                        } else {
                            return Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        return Page.empty(pageable);
                    }
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
                        if (bookingSortFields.contains(sortParam)) {
                            return searchByPaymentDateWithBookingSort(paymentDate, pageable);
                        } else {
                            return searchByPaymentDateWithPaymentSort(paymentDate, pageable);
                        }

                    } catch (DateTimeParseException e) {
                        return Page.empty(pageable);
                    }
                default:
                    return Page.empty(pageable);
            }
        }
    }
}
