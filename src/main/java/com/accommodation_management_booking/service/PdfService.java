package com.accommodation_management_booking.service;


import com.accommodation_management_booking.entity.Booking;
import com.accommodation_management_booking.repository.BookingRepository;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    @Autowired
    private BookingRepository bookingRepository;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public ByteArrayInputStream createPdf(int id) throws IOException {
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
        context.setVariable("representedBy", "booking.getUser().getRepresentedBy()");
        context.setVariable("workUnit", "booking.getUser().getWorkUnit()");
        context.setVariable("position", "booking.getUser().getPosition()");
        context.setVariable("phoneNumberA", "booking.getUser().getPhoneNumberA()");

        // Render HTML từ template
        String html = templateEngine.process("contract_template", context);

        // Chuyển đổi HTML thành PDF
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf(html, target, converterProperties);

        return new ByteArrayInputStream(target.toByteArray());
    }
}