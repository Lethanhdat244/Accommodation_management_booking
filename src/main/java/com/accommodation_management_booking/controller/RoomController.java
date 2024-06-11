package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.RoomDTO;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.service.RoomNotFoundException;
import com.accommodation_management_booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/fpt-dorm/admin")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
private RoomRepository repoRooom;


    @GetMapping("/list_room")
    public String roomTable(Model model) {
        List<RoomDTO> roomDTOs = roomService.getAllRooms();
        model.addAttribute("roomList", roomDTOs);
        return "admin_list_room";
    }

    @GetMapping("/add_room")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new RoomDTO());
        return "admin_add_new_room";
    }

    @PostMapping("/saveRoom")
    public String saveRoom(RoomDTO roomDTO, RedirectAttributes ra) {

        roomService.saveRoom(roomDTO, ra);

        return "redirect:/fpt-dorm/admin/add_room";
    }

    @GetMapping("/room_detail/{roomId}")
    public String roomDetail(@PathVariable("roomId") int roomId, Model model) {
        RoomDTO roomDTO = roomService.getRoomDetail(roomId, model);
        if (roomDTO != null) {
            model.addAttribute("room", roomDTO);
            return "admin_room_detail"; // Trả về trang admin_room_detail để hiển thị thông tin chi tiết của phòng
        } else {
            // Xử lý trường hợp không tìm thấy phòng với roomId tương ứng
            return "redirect:/fpt-dorm/admin/list_room"; // Hoặc chuyển hướng đến trang danh sách phòng
        }
    }
    @GetMapping("/editRoom/{id}")
    public String editRoom(Model model, @PathVariable int id) {
        RoomDTO roomDTO = roomService.getRoomDetail(id, model);
        model.addAttribute("room", roomDTO);
        return "admin_update_room";  // Sử dụng tên view mới
    }



    @PostMapping("/editRoom/save")
    public String saveEditedRoom(RoomDTO roomDTO, RedirectAttributes ra) {
        try {
            roomService.updateRoom(roomDTO);
            ra.addFlashAttribute("message", "The Room has been updated successfully");
        } catch (RoomNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/fpt-dorm/admin/list_room"; // Redirect to the room list page
        }
        return "redirect:/fpt-dorm/admin/list_room"; // Redirect to the room list page
    }

    @GetMapping("/deleteRoom/{roomId}")
    public String deleteRoom(@PathVariable int roomId, RedirectAttributes ra) {
        roomService.deleteRoom(roomId, ra);
        return "redirect:/fpt-dorm/admin/list_room";
    }

    @GetMapping("/search")
    public String searchRoomNumber(Model model,
                                   @RequestParam(value = "keyword", required = false) String keyword) {
        List<RoomDTO> rooms = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            rooms = roomService.getAllRooms();
        } else {
            rooms = roomService.findByRoomNumber(keyword);
            if (rooms.isEmpty()) {
                model.addAttribute("message", "Room number không có trong hệ thống.");
            }
        }
        model.addAttribute("roomList", rooms);
        model.addAttribute("keyword", keyword);

        return "admin_list_room";

    }

}
