package com.accommodation_management_booking.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetLink);
    public void sendCompleteRegistrationEmail(String email, String content);
    void sendRegistrationEmail(String email, String content);
}
