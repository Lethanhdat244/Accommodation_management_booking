package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.DormBedInfoDTO;

import java.util.List;

public interface DormService {
    DormBedInfoDTO getDormBedInfo(Integer dormId);
    List<DormBedInfoDTO> getAllDormBedInfo();
}
