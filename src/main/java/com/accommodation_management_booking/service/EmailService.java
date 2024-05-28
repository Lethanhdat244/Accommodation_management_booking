package com.accommodation_management_booking.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetLink);
}
