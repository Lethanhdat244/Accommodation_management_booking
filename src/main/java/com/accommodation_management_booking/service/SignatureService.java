package com.accommodation_management_booking.service;

import com.accommodation_management_booking.config.CloudinaryConfiguration;
import com.accommodation_management_booking.entity.Contract;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.ContractRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class SignatureService {

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private BookingRepository bookingRepository;

    public String addSignatureAndUpload(File pdfFile, String signatureDataUrl, int bookingId) throws IOException {
        // Load the existing PDF
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
        Document document = new Document(pdfDoc);

        // Decode the signature data URL to an image
        String base64Image = signatureDataUrl.split(",")[1];
        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
        Image signatureImage = new Image(com.itextpdf.io.image.ImageDataFactory.create(imageBytes));

        // Add signature image to PDF (you need to adjust the position and size)
        document.add(new Paragraph("Signature:"));
        document.add(signatureImage);

        // Close the document
        document.close();

        // Upload PDF to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(pdfFile, ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "PDF Contracts/"  // Folder in Cloudinary where you want to store
        ));

        // Save contract info to database
        Contract newContract = new Contract();
        newContract.setBooking(bookingRepository.findById(bookingId).orElse(null));
        newContract.setContractLink((String) uploadResult.get("url"));
        contractRepository.save(newContract);

        // Clean up temporary file
        pdfFile.delete();

        return (String) uploadResult.get("url");
    }
}
