package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.DormRepository;
import com.accommodation_management_booking.service.DormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final BedRepository bedRepository;


    @Autowired
    public DormServiceImpl(DormRepository dormRepository, BedRepository bedRepository) {
        this.dormRepository = dormRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public DormBedInfoDTO getDormBedInfo(Integer dormId) {
        Dorm dorm = dormRepository.findById(dormId).orElse(null);
        if (dorm == null) {
            return null;
        }

        List<Bed> beds = bedRepository.findByRoom_Floor_Dorm_DormId(dormId);
        int totalBeds = beds.size();
        int usedBeds = (int) beds.stream().filter(bed -> !bed.getIsAvailable()).count();
        int availableBeds = totalBeds - usedBeds;

        return new DormBedInfoDTO(dorm.getDormId(), dorm.getDormName(), totalBeds, usedBeds, availableBeds);
    }

    @Override
    public List<DormBedInfoDTO> getAllDormBedInfo() {
        List<Dorm> dorms = dormRepository.findAll();
        return dorms.stream().map(dorm -> {
            List<Bed> beds = bedRepository.findByRoom_Floor_Dorm_DormId(dorm.getDormId());
            int totalBeds = beds.size();
            int usedBeds = (int) beds.stream().filter(bed -> !bed.getIsAvailable()).count();
            int availableBeds = totalBeds - usedBeds;
            return new DormBedInfoDTO(dorm.getDormId(), dorm.getDormName(), totalBeds, usedBeds, availableBeds);
        }).collect(Collectors.toList());
    }


}
