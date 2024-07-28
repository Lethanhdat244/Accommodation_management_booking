package com.accommodation_management_booking.dto;

public class SignatureRequest {
    private int bookingId;
    private String signatureDataUrl;

    // Getters and setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getSignatureDataUrl() {
        return signatureDataUrl;
    }

    public void setSignatureDataUrl(String signatureDataUrl) {
        this.signatureDataUrl = signatureDataUrl;
    }
}
