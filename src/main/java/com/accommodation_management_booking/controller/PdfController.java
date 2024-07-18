//package com.accommodation_management_booking.controller;
//
//import com.accommodation_management_booking.service.PdfService;
//import com.accommodation_management_booking.service.SignatureService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api")
//public class PdfController {
//
//    @Autowired
//    private PdfService pdfService;
//
//    @Autowired
//    private SignatureService signatureService;
//
//    // Endpoint để tạo file PDF từ dữ liệu đặt chỗ
//    @PostMapping("/generate-pdf")
//    public ResponseEntity<String> generatePdf(@RequestParam int bookingId) {
//        try {
//            // Tạo file PDF và trả về đường dẫn tạm thời của file
//            File pdfFile = pdfService.createPdfFile(bookingId);
//            return ResponseEntity.ok(pdfFile.getPath()); // Trả về đường dẫn của file PDF
//        } catch (IOException e) {
//            e.printStackTrace(); // Ghi log lỗi
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating PDF");
//        }
//    }
//
//    // Endpoint để thêm chữ ký vào file PDF và tải lên Cloudinary
//    @PostMapping("/add-signature")
//    public ResponseEntity<String> addSignature(@RequestParam int bookingId, @RequestParam String signatureDataUrl) {
//        try {
//            // Tạo file PDF từ dữ liệu đặt chỗ
//            File pdfFile = new File(pdfService.createPdfFile(bookingId).getPath());
//
//            // Thêm chữ ký vào file PDF và tải lên Cloudinary
//            String pdfUrl = signatureService.addSignatureAndUpload(pdfFile, signatureDataUrl, bookingId);
//
//            return ResponseEntity.ok(pdfUrl); // Trả về URL của file PDF trên Cloudinary
//        } catch (IOException e) {
//            e.printStackTrace(); // Ghi log lỗi
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding signature");
//        }
//    }
//}
