package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.SignatureRequest;
import com.accommodation_management_booking.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SignatureController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/add-signature")
    public ResponseEntity<Map<String, String>> addSignature(@RequestBody SignatureRequest signatureRequest) {
        try {
            // Tạo PDF từ template và upload lên Cloudinary
            String pdfUrl = pdfService.addSignatureAndUpload(signatureRequest.getBookingId(), signatureRequest.getSignatureDataUrl());

            // Trả về URL của PDF đã upload
            Map<String, String> response = new HashMap<>();
            response.put("url", pdfUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về lỗi
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
