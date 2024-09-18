package com.accommodation_management_booking.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfiguration {

    @Bean
    public Cloudinary cloudinaryConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "");
        config.put("api_key", "");
        config.put("api_secret", "");
        config.put("secure", "");

        return new Cloudinary(config);
    }

    public Cloudinary cloudinaryConfigPDF() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "");
        config.put("api_key", "");
        config.put("api_secret", "");
        config.put("secure", "");

        Cloudinary cloudinary = null;
        try {
            cloudinary = new Cloudinary(config);
        } catch (Exception e) {
            // Xử lý exception tại đây, ví dụ: ghi log hoặc thông báo lỗi
            e.printStackTrace();
            // hoặc có thể throw lại exception để thông báo lỗi ra ngoài
            // throw new RuntimeException("Error initializing Cloudinary: " + e.getMessage(), e);
        }

        return cloudinary;
    }
}
