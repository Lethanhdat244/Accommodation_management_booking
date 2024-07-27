package com.accommodation_management_booking.service;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenService {

    private static final ConcurrentMap<String, Boolean> usedTokens = new ConcurrentHashMap<>();

    // Mã hóa bookingId
    public static String encode(int bookingId) {
        return Base64.getUrlEncoder().encodeToString(String.valueOf(bookingId).getBytes());
    }

    // Giải mã token
    public static int decode(String encodedId) {
        String idAsString = new String(Base64.getUrlDecoder().decode(encodedId));
        return Integer.parseInt(idAsString);
    }

    // Kiểm tra token đã được sử dụng chưa
    public static boolean isTokenUsed(String token) {
        return usedTokens.getOrDefault(token, false);
    }

    // Đánh dấu token là đã được sử dụng
    public static void markTokenAsUsed(String token) {
        usedTokens.put(token, true);
    }
}

