package com.accommodation_management_booking.service;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.entity.Dorm;

import java.util.List;

public interface DormService {
    DormBedInfoDTO getDormBedInfo(Integer dormId);
    List<DormBedInfoDTO> getAllDormBedInfo();

    List<Dorm> getAllDorms();

    Dorm getDormById(Integer dormId);

    Dorm saveDorm(Dorm dorm);
    void deleteDorm(Integer dormId);

    public void updateDorm(Dorm dorm);
//    Dorm getDormByName(String dormName);
}
