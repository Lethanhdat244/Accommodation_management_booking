package com.accommodation_management_booking.dto;

import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Bed.MaintenanceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BedDTO {
    private Integer bedId;
    private Integer roomId;
    private Integer bedNumber;
    private Boolean isAvailable;
    private MaintenanceStatus maintenanceStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for converting from Bed entity to BedDTO
    public BedDTO(Bed bed) {
        this.bedId = bed.getBedId();
        this.roomId = bed.getRoom().getRoomId(); // Assuming Room has a roomId field
        this.bedNumber = bed.getBedNumber();
        this.isAvailable = bed.getIsAvailable();
        this.maintenanceStatus = bed.getMaintenanceStatus();
        this.createdAt = bed.getCreatedAt();
        this.updatedAt = bed.getUpdatedAt();
    }

    // Additional constructors if needed
    public BedDTO() {
        // Default constructor
    }
}
