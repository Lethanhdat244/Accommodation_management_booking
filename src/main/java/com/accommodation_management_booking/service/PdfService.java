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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PdfService {

    private final TemplateEngine templateEngine;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ContractRepository contractRepository;

    private final JavaMailSender emailSender;

    private final Cloudinary cloudinary;

    @Autowired
    public PdfService(TemplateEngine templateEngine, CloudinaryConfiguration cloudinaryConfig, JavaMailSender emailSender) {
        this.templateEngine = templateEngine;
        this.cloudinary = cloudinaryConfig.cloudinaryConfigPDF();  // Sử dụng Cloudinary config đã tạo
        this.emailSender = emailSender;
    }

    public String addSignatureAndUpload(int bookingId, String signatureDataUrl) throws IOException {
        // Tạo PDF từ template
        byte[] pdfBytes = createPdfFromTemplate(bookingId);

        // Chuyển đổi Data URL thành Byte Array
        byte[] signatureBytes = java.util.Base64.getDecoder().decode(signatureDataUrl.split(",")[1]);

        // Tạo file PDF với chữ ký
        ByteArrayOutputStream finalPdfOutputStream = new ByteArrayOutputStream();
        try (PdfWriter finalPdfWriter = new PdfWriter(finalPdfOutputStream);
             PdfDocument finalPdfDoc = new PdfDocument(finalPdfWriter);
             PdfDocument existingPdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)))) {

            // Sao chép các trang từ PDF hiện có
            existingPdfDoc.copyPagesTo(1, existingPdfDoc.getNumberOfPages(), finalPdfDoc);

            // Lấy kích thước trang
            PageSize pageSize = finalPdfDoc.getDefaultPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();

            // Thêm chữ ký vào trang 4 nếu có
            if (finalPdfDoc.getNumberOfPages() >= 4) {
                ImageData imageData = ImageDataFactory.create(signatureBytes);
                Image signatureImage = new Image(imageData);
                float signatureWidth = signatureImage.getImageWidth();
                float signatureHeight = signatureImage.getImageHeight();

                // Tính toán vị trí để đặt chữ ký vào góc phải dưới cùng của trang
                float x = (pageWidth - signatureWidth) / 2; // 20px từ cạnh phải
                float y = 390; // 20px từ cạnh dưới

                PdfPage page = finalPdfDoc.getPage(4); // Lấy trang thứ 4
                PdfCanvas pdfCanvas = new PdfCanvas(page);

                // Vẽ chữ ký vào trang
                pdfCanvas.addImage(imageData, signatureWidth, 0, 0, signatureHeight, x, y);
            }
        }

        // Tạo file và tải lên Cloudinary
        String filename = "contract_" + bookingId + ".pdf";
        File tempFile = File.createTempFile(filename, ".pdf");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(finalPdfOutputStream.toByteArray());
        }

        // Tải PDF lên Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "resource_type", "raw",
                "folder", "PDF Contracts/"
        ));

        // Lưu thông tin hợp đồng vào cơ sở dữ liệu
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        Booking booking = optionalBooking.get();
        Contract newContract = new Contract();
        newContract.setBooking(booking);
        newContract.setContractLink((String) uploadResult.get("url"));
        contractRepository.save(newContract);

        // Xóa file tạm
        tempFile.delete();

        sendEmailWithPdfLink(booking, (String) uploadResult.get("url"));

        return (String) uploadResult.get("url");
    }


    public byte[] createPdfFromTemplate(int bookingId) throws IOException {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
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

        // Render HTML from template
        String html = templateEngine.process("contract_template", context);

        // Convert HTML to PDF
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf(html, pdfOutputStream, converterProperties);

        return pdfOutputStream.toByteArray();
    }

    private void sendEmailWithPdfLink(Booking booking, String pdfUrl) {
        // Tạo nội dung email
        String body = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                "Thank you for using our service.\n\n" +
                "You can download your contract here: " + pdfUrl;

        // Tạo đối tượng SimpleMailMessage
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getUser().getEmail());
        message.setSubject("Your Booking Contract");
        message.setText(body);

        // Gửi email
        emailSender.send(message);
    }
}
