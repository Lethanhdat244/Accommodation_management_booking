package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.dto.RoomBedUsage;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.service.DormService;
import com.accommodation_management_booking.service.FloorService;
import com.accommodation_management_booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DormController {

    private final DormService dormService;

    private final FloorService floorService;

    private final RoomService roomService;

    private final FloorRepository floorRepository;

    @Autowired
    public DormController(DormService dormService, FloorService floorService, RoomService roomService, FloorRepository floorRepository) {
        this.dormService = dormService;
        this.floorService = floorService;
        this.roomService = roomService;
        this.floorRepository = floorRepository;
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

    @GetMapping("/fpt-dorm/user/room-list-used/{floorId}")
    public String floorDetail(@PathVariable("floorId") int floorId, Model model) {
        List<RoomBedUsage> roomBedUsageList = roomService.getRoomBedUsageByFloorId(floorId);
        model.addAttribute("roomBedUsageList", roomBedUsageList);
        return "used_room_list";
    }




    @GetMapping("/fpt-dorm/admin/view-dorm-list")
    public String listDorms(Model model) {
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        return "admin/dorm-manager/admin_dorm_list"; // Thymeleaf view name
    }


    @GetMapping("/fpt-dorm/admin/add-dorm-form")
    public String showFormForAdd(Model model) {
        Dorm dorm = new Dorm();
        model.addAttribute("dorm", dorm);
        return "admin/dorm-manager/admin_add_dorm_form";
    }


    @PostMapping("/fpt-dorm/admin/save")
    public String saveDorm(@ModelAttribute("dorm") Dorm dorm, Model model) {
        try {
            dormService.saveDorm(dorm);
            return "redirect:/fpt-dorm/admin/view-dorm-list";
        }catch (Exception e){
            model.addAttribute("error", "Dorm đã có sẵn");
            return "admin/dorm-manager/admin_add_dorm_form";
        }
    }

    @GetMapping("/fpt-dorm/admin/delete-dorm")
    public String deleteDorm(@RequestParam("dormId") Integer dormId, Model model) {
        try {
            dormService.deleteDorm(dormId);
            return "redirect:/fpt-dorm/admin/view-dorm-list";
        }catch (Exception e){
            model.addAttribute("error", "Trong dorm đang có phòng đang sử dụng, bạn không thể xóa nó");
            return "admin/dorm-manager/admin_dorm_list";
        }
    }

    @GetMapping("/fpt-dorm/admin/edit-dorm-form/{dormId}")
    public String showFormForUpdate(@PathVariable("dormId") int dormId, Model model) {
        Dorm dorm = dormService.getDormById(dormId);
        model.addAttribute("dorm", dorm);
        return "admin/dorm-manager/admin_edit_dorm_form";
    }

    @PostMapping("/fpt-dorm/admin/update-dorm")
    public String updateDorm(@ModelAttribute("dorm") Dorm dorm, Model model) {
        try {
            dormService.updateDorm(dorm);
            return "redirect:/fpt-dorm/admin/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update dorm.");
            return "admin/dorm-manager/admin_edit_dorm_form";
        }
    }


    @GetMapping("/fpt-dorm/admin/view-floor-list/{dormId}")
    public String adminFloorList(@PathVariable("dormId") int dormId, Model model) {
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        model.addAttribute("dormId", dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        return "admin/dorm-manager/admin_floor_list";
    }

    @GetMapping("/fpt-dorm/admin/add-floor-form/{dormId}")
    public String AddFloorForm(@PathVariable("dormId") int dormId, Model model) {
        Dorm dorm = dormService.getDormById(dormId); // Get Dorm object by dormId
        Floor floor = new Floor();
        floor.setDorm(dorm); // Set the dorm to the floor
        model.addAttribute("floor", floor);
        return "admin/dorm-manager/admin_add_floor_form";
    }



    @PostMapping("/fpt-dorm/admin/add-floor-form/{dormId}")
    public String addFloor(@PathVariable("dormId") int dormId, @ModelAttribute("floor") Floor floor, Model model) {
        try {
            // Kiểm tra xem số tầng đã tồn tại cho dormId này chưa
            if (floorService.isFloorNumberDuplicate(dormId, floor.getFloorNumber())) {
                model.addAttribute("error", "Floor number already exists for this dorm.");
                return "admin/dorm-manager/admin_add_floor_form";
            }

            // Nếu không trùng, tiến hành thêm tầng mới
            floorService.addFloor(floor);
            return "redirect:/fpt-dorm/admin/view-floor-list/{dormId}";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add floor.");
            return "admin/dorm-manager/admin_add_floor_form";
        }
    }
    @GetMapping("/fpt-dorm/admin/delete-floor/{floorId}")
    public String deleteFloor(@PathVariable("floorId") int floorId, Model model) {
        try {
            Floor floor = floorService.getFloorById(floorId);
            if (floor != null) {
                int dormId = floor.getDorm().getDormId();
                floorService.deleteFloor(floorId);
                return "redirect:/fpt-dorm/admin/view-floor-list/" + dormId;
            } else {
                model.addAttribute("error", "Floor not found.");
                return "admin/dorm-manager/admin_floor_list";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi.");
            return "admin/dorm-manager/admin_floor_list";
        }
    }

    @GetMapping("/fpt-dorm/admin/edit-floor-form/{floorId}")
    public String showEditFloorForm(@PathVariable("floorId") int floorId, Model model) {
        Floor floor = floorService.getFloorById(floorId);
        if (floor != null) {
            model.addAttribute("floor", floor);
            return "admin/dorm-manager/admin_edit_floor_form";
        } else {
            model.addAttribute("error", "Floor not found.");
            return "admin/dorm-manager/admin_floor_list";
        }
    }

    @PostMapping("/fpt-dorm/admin/update-floor")
    public String updateFloor(@ModelAttribute("floor") Floor floor, Model model) {
        try {
            if (floorService.isFloorNumberDuplicateInDorm(floor.getDorm().getDormId(), floor.getFloorNumber(), floor.getFloorId())) {
                model.addAttribute("error", "Floor number already exists for this dorm.");
                return "admin/dorm-manager/admin_edit_floor_form";
            }

            floorService.updateFloor(floor);
            return "redirect:/fpt-dorm/admin/view-floor-list/" + floor.getDorm().getDormId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update floor.");
            return "admin/dorm-manager/admin_edit_floor_form";
        }
    }

}
