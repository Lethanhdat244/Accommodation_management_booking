package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.service.DormService;
import com.accommodation_management_booking.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DormController {

    private final DormService dormService;

    private final FloorService floorService;

    @Autowired
    public DormController(DormService dormService, FloorService floorService) {
        this.dormService = dormService;
        this.floorService = floorService;
    }

    @GetMapping("/fpt-dorm/user/detail-dorm")
    public String getAllDormBedInfo(Model model) {
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        return "available_beds"; // Thymeleaf view name
    }

    @GetMapping("/fpt-dorm/user/floor-list/{dormId}")
    public String dormDetail(@PathVariable("dormId") int dormId, Model model) {
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        return "floor_list";
    }
}
