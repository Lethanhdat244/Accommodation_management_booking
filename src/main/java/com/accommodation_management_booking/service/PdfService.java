package com.accommodation_management_booking.service;

import com.accommodation_management_booking.config.CloudinaryConfiguration;
import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.entity.Contract;
import com.accommodation_management_booking.repository.BookingRepository;
import com.accommodation_management_booking.repository.ContractRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ContractRepository contractRepository;

    private final Cloudinary cloudinary;

    @Autowired
    public PdfService(TemplateEngine templateEngine, CloudinaryConfiguration cloudinaryConfig) {
        this.templateEngine = templateEngine;
        this.cloudinary = cloudinaryConfig.cloudinaryConfigPDF();  // Sử dụng Cloudinary config đã tạo
    }

    public String createPdfAndUpload(int id) throws IOException {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        Booking booking = optionalBooking.get();
        LocalDate now = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");

        // Prepare Thymeleaf context variables
        Context context = new Context();
        context.setVariable("day", now.getDayOfMonth());
        context.setVariable("month", now.format(monthFormatter));
        context.setVariable("year", now.getYear());
        context.setVariable("studentName", booking.getUser().getUsername());
        context.setVariable("gender", booking.getUser().getGender());
        context.setVariable("dob", booking.getUser().getBirthdate());
        context.setVariable("studentId", booking.getUser().getUserId());
        context.setVariable("className", "class"); // Replace with actual class info
        context.setVariable("faculty", "faculty"); // Replace with actual faculty info
        context.setVariable("graduationYear", "graduationYear"); // Replace with actual graduation year info
        context.setVariable("phoneNumberB", booking.getUser().getPhoneNumber());
        context.setVariable("email", booking.getUser().getEmail());
        context.setVariable("address", booking.getUser().getAddress());
        context.setVariable("university", "university"); // Replace with actual university info
        context.setVariable("roomNumber", booking.getRoom().getRoomNumber());
        context.setVariable("floor", booking.getRoom().getFloor().getFloorNumber());
        context.setVariable("building", booking.getRoom().getFloor().getDorm().getDormName());
        context.setVariable("rentalPrice", booking.getTotalPrice());
        context.setVariable("rentalStart", booking.getStartDate());
        context.setVariable("rentalEnd", booking.getEndDate());
        context.setVariable("consumptionLimit", ""); // Replace with actual consumption limit
        context.setVariable("representedBy", "");
        context.setVariable("workUnit", "");
        context.setVariable("position", "");
        context.setVariable("phoneNumberA", "");

        // Render HTML từ template
        String html = templateEngine.process("contract_template", context);

        // Chuyển đổi HTML thành PDF
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf(html, target, converterProperties);
        String filename = "contract_" + booking.getUser().getUserId() + "_" + booking.getBookingId();

        // Lưu PDF tạm thời
        File tempFile = File.createTempFile(filename, ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(target.toByteArray());
        }

        // Upload PDF lên Cloudinary
        Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "PDF Contracts/"  // Thư mục trên Cloudinary bạn muốn lưu trữ
        ));

        Contract newContract = new Contract();
        newContract.setBooking(booking);
        newContract.setContractLink((String) uploadResult.get("url"));
        contractRepository.save(newContract);

        // Xóa file tạm
        tempFile.delete();

        return (String) uploadResult.get("url");
    }
}
