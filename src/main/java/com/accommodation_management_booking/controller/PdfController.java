package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/generate-pdf/{id}")
    public String generatePdf(@PathVariable int id) {
        try {
            return pdfService.createPdfAndUpload(id);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
