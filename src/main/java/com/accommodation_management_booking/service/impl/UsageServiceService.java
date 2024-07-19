package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.UsageService;
import com.accommodation_management_booking.repository.UsageServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsageServiceService {
    @Autowired
    UsageServiceRepository usageServiceRepository;

    public void saveUsageService(UsageService usageService) {
        try {
            usageServiceRepository.save(usageService);
            System.out.println("Send usage service record to " + usageService.getUser().getEmail());  // Debug statement
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save complain", e);
        }
    }

    public List<UsageService> getAllUsageServices() {
        return usageServiceRepository.findAll();
    }

    public List<UsageService> getUsageServicesByMonthAndYear(int selectedMonth, int selectedYear) {
        LocalDateTime startDate = LocalDateTime.of(selectedYear, selectedMonth, 1, 0, 0); // Ngày đầu tháng
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1); // Ngày cuối tháng

        return usageServiceRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<Integer> getAllYears() {
        return usageServiceRepository.findAllYears();
    }
}
