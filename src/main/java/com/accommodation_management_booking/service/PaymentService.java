package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.PaymentTransactionDTO;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PaymentService {
    public Page<User> getPayments(Pageable pageable);

    public Page<User> searchAll(String keyword, Pageable pageable);

    public Page<User> searchByUser(int userId, Pageable pageable);

    public Page<User> searchByName(String keyword, Pageable pageable);

    public Page<User> searchByMail(String keyword, Pageable pageable);

    public Page<User> searchByPhone(String keyword, Pageable pageable);

    public Page<PaymentTransactionDTO> searchByUserWithPaymentSort(int userId, Pageable pageable);

    public Page<PaymentTransactionDTO> searchByUserWithBookingSort(int userId, Pageable pageable);

    public PaymentTransactionDTO findByPaymentId(int id);

    public Page<PaymentTransactionDTO> searchByStatusWithPaymentSort(Booking.Status status, Pageable pageable);

    public Page<PaymentTransactionDTO> searchByStatusWithBookingSort(Booking.Status status, Pageable pageable);

    public Page<PaymentTransactionDTO> findPendingPaymentsByUserEmail(String email, Pageable pageable);

    public Page<PaymentTransactionDTO> findPaymentsByUserEmail(String email, Pageable pageable);

    public Page<PaymentTransactionDTO> findByPaymentIdWithPage(int paymentId, Pageable pageable);

    public Page<PaymentTransactionDTO> findByPaymentDateWithPage(LocalDateTime paymentDate, int userId, Pageable pageable);
}
