package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.dto.FloorBedUsage;
import com.accommodation_management_booking.dto.RoomBedUsage;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.Floor;
import com.accommodation_management_booking.entity.Room;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.FloorRepository;
import com.accommodation_management_booking.repository.RoomRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.BedService;
import com.accommodation_management_booking.service.DormService;
import com.accommodation_management_booking.service.FloorService;
import com.accommodation_management_booking.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class DormController {

    private final DormService dormService;

    private final FloorService floorService;

    private final RoomService roomService;

    private final BedService bedService;

    private final FloorRepository floorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    public DormController(DormService dormService, FloorService floorService, RoomService roomService, BedService bedService, FloorRepository floorRepository) {
        this.dormService = dormService;
        this.floorService = floorService;
        this.roomService = roomService;
        this.bedService = bedService;
        this.floorRepository = floorRepository;
    }

    @GetMapping("/fpt-dorm/user/detail-dorm")
    public String getAllDormBedInfo(Model model, HttpSession session) {
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo1();
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        model.addAttribute("role", session.getAttribute("role"));

        return "user/available_bed";
    }

    @GetMapping("/fpt-dorm/user/floor-list/{dormId}")
    public String dormDetail(@PathVariable("dormId") int dormId, Model model, HttpSession session) {
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName" , dormName);
        model.addAttribute("role", session.getAttribute("role"));
        return "user/floor_list";
    }


    @GetMapping("/fpt-dorm/user/room-list-used/{dormId}/{floorId}")
    public String floorDetail(@PathVariable("dormId") int dormId, @PathVariable("floorId") int floorId, Model model, HttpSession session) {
        List<RoomBedUsage> roomBedUsageList = roomService.getRoomBedUsageByFloorId(floorId);
        model.addAttribute("roomBedUsageList", roomBedUsageList);
        model.addAttribute("role", session.getAttribute("role"));
        return "user/used_room_list";
    }


    @GetMapping("/fpt-dorm/admin/view-dorm-list")
    public String listDorms(Model model) {
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        Collections.sort(dormBedInfoList, (d1, d2) -> d1.getDormName().compareTo(d2.getDormName()));
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        Dorm dorm = new Dorm();
        model.addAttribute("dorm", dorm);
        return "admin/dorm-manager/admin_dorm_list"; // Thymeleaf view name
    }


    @PostMapping("/fpt-dorm/admin/save")
    public String saveDorm(@ModelAttribute("dorm") Dorm dorm, Model model) {
        try {
            dormService.saveDorm(dorm);
            return "redirect:/fpt-dorm/admin/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/dorm-manager/admin_dorm_list";
        }
    }

    @GetMapping("/fpt-dorm/admin/delete-dorm/{dormId}")
    public String deleteDorm(@PathVariable("dormId") int dormId, Model model) {
        try {
            dormService.deleteDorm(dormId);
            return "redirect:/fpt-dorm/admin/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", "In a dorm room that is in use, you cannot delete it.");
            // Trả về lại view admin_dorm_list để hiển thị lỗi
            List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
            model.addAttribute("dormBedInfoList", dormBedInfoList);
            Dorm dorm = new Dorm();
            model.addAttribute("dorm", dorm);
            return "admin/dorm-manager/admin_dorm_list";
        }
    }


    @GetMapping("/fpt-dorm/admin/edit-dorm-form/{dormId}")
    public String showFormForUpdate(@PathVariable("dormId") int dormId, Model model) {
        Dorm dorm = dormService.getDormById(dormId);
        model.addAttribute("dorm", dorm);
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        Collections.sort(dormBedInfoList, (d1, d2) -> d1.getDormName().compareTo(d2.getDormName()));
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        return "admin/dorm-manager/admin_edit_dorm_form"; // Thymeleaf view name
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
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);
        model.addAttribute("dormId", dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        return "admin/dorm-manager/admin_floor_list";
    }

    @GetMapping("/fpt-dorm/admin/add-floor-form/{dormId}")
    public String addFloor(@PathVariable int dormId) {
        floorService.addFloor(dormId);
        return "redirect:/fpt-dorm/admin/view-floor-list/{dormId}";
    }


    @GetMapping("/fpt-dorm/admin/delete-floor/{floorId}")
    public String deleteFloor(@PathVariable("floorId") int floorId, RedirectAttributes redirectAttributes) {
        // Lấy thông tin tầng cần xóa
        Floor floor = floorService.getFloorById(floorId);
        if (floor == null) {
            // Tầng không tồn tại
            redirectAttributes.addFlashAttribute("error", "Floor not found.");
            return "redirect:/fpt-dorm/admin/view-floor-list";
        }

        // Lấy tầng cao nhất trong cùng một ký túc xá
        Floor highestFloor = floorService.getHighestFloorByDormId(floor.getDorm().getDormId());

        // Kiểm tra nếu tầng cần xóa có phải là tầng cao nhất không
        if (floor.getFloorNumber().equals(highestFloor.getFloorNumber())) {
            // Kiểm tra nếu tầng có phòng nào không
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            if (rooms != null && !rooms.isEmpty()) {
                // Tầng có chứa phòng, không cho phép xóa
                redirectAttributes.addFlashAttribute("error", "The floor cannot be deleted because it contains rooms.");
            } else {
                // Xóa tầng nếu không có phòng
                floorService.deleteFloor(floorId);
            }
        } else {
            // Tầng không phải là tầng cao nhất, không cho phép xóa
            redirectAttributes.addFlashAttribute("error", "Only the highest floor can be deleted.");
        }

        // Chuyển hướng về danh sách tầng
        return "redirect:/fpt-dorm/admin/view-floor-list/" + floor.getDorm().getDormId();
    }



    @GetMapping("/fpt-dorm/admin/edit-floor-form/{dormId}/{floorId}")
    public String showEditFloorForm(@PathVariable("dormId") int dormId,
                                    @PathVariable("floorId") int floorId,
                                    Model model) {
        Floor floor = floorService.getFloorById(floorId);
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        model.addAttribute("dormId", dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);
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
            if (floor.getFloorNumber() < 0) {
                model.addAttribute("error", "Floor number cannot be negative.");
                return "admin/dorm-manager/admin_edit_floor_form";
            }

            if (floorService.isFloorNumberDuplicateInDorm(floor.getDorm().getDormId(), floor.getFloorNumber(), floor.getFloorId())) {
                model.addAttribute("error", "Floor number already exists for this dorm.");
                return "admin/dorm-manager/admin_edit_floor_form";
            }

            floorService.updateFloor(floor);

            // Retrieve updated floor list after update
            List<Floor> updatedFloorList = floorService.getFloorsByDormId(floor.getDorm().getDormId());
            model.addAttribute("dormId", floor.getDorm().getDormId());
            model.addAttribute("floorList", updatedFloorList);

            return "redirect:/fpt-dorm/admin/view-floor-list/" + floor.getDorm().getDormId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update floor.");
            return "admin/dorm-manager/admin_edit_floor_form";
        }
    }


    @GetMapping("/fpt-dorm/admin/view-rooms/{dormId}/{floorId}")
    public String viewRooms(@PathVariable int dormId, @PathVariable int floorId, Model model) {
        try {
            Floor floor = floorService.getFloorById(floorId);
            if (floor != null) {
                List<Room> rooms = roomService.getRoomsByFloorId(floorId);

                // Sắp xếp danh sách phòng theo roomName (tên phòng)
                rooms.sort(Comparator.comparing(Room::getRoomNumber));

                model.addAttribute("floor", floor);
                model.addAttribute("rooms", rooms);
                model.addAttribute("dormId", dormId);
                String dormName = dormService.getDormNameById(dormId);
                model.addAttribute("dormName", dormName);
                return "admin/dorm-manager/admin_list_rooms"; // Trang view_rooms để hiển thị danh sách các phòng
            } else {
                model.addAttribute("error", "Floor not found.");
                return "admin/dorm-manager/admin_floor_list";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to view rooms: " + e.getMessage());
            return "admin/dorm-manager/admin_floor_list";
        }
    }


    @GetMapping("/fpt-dorm/admin/add-room/{dormId}/{floorId}")
    public String showAddRoomForm(@PathVariable int dormId, @PathVariable int floorId, Model model) {
        model.addAttribute("floorId", floorId);
        model.addAttribute("dormId", dormId);
        model.addAttribute("room", new Room());
        List<Room> rooms = roomService.getRoomsByFloorId(floorId);
        // Sắp xếp danh sách phòng theo roomName (tên phòng)
        rooms.sort(Comparator.comparing(Room::getRoomNumber));
        model.addAttribute("rooms", rooms);
        Floor floor = floorService.getFloorById(floorId);
        model.addAttribute("floor", floor);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);

        try {
            model.addAttribute("floorNumber", floor.getFloorNumber());

        } catch (RuntimeException e) {
            model.addAttribute("error", "Fail to add room");

            return "admin/dorm-manager/admin_add_room"; // Hoặc trang lỗi khác
        }

        return "admin/dorm-manager/admin_add_room";
    }


    @PostMapping("/fpt-dorm/admin/add-room/{dormId}/{floorId}")
    public String addRoomToFloor(@PathVariable int dormId, @PathVariable int floorId, @ModelAttribute Room room, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Validate room capacity
            if (room.getCapacity() < 0) {
                redirectAttributes.addFlashAttribute("error", "Capacity cannot be less than 0");
                return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
            }

            // Validate room price per bed
            if (room.getPricePerBed() < 0) {
                redirectAttributes.addFlashAttribute("error", "Price per bed cannot be less than 0");
                return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
            }

            // Validate if the room number already exists in the given floor
            boolean roomExists = roomService.existsByRoomNumberAndFloorId(room.getRoomNumber(), floorId);
            if (roomExists) {
                redirectAttributes.addFlashAttribute("error", "Room number already exists in the given floor");
                return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
            }

            // Add room to floor
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            if (rooms.size() >= 10) {
                redirectAttributes.addFlashAttribute("error", "The floor already has 10 rooms. Cannot add more.");
                return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
            }
            roomService.addRoomToFloor(floorId, room);

            // Create and add beds to the room based on the capacity
            for (int i = 1; i <= room.getCapacity(); i++) {
                String bedName = room.getRoomNumber() + "-" + i; // Create bed names like A101-1, A101-2, ...
                bedService.addBedToRoom(room.getRoomId(), bedName);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Room and beds added successfully");
            return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Fail to add room");
            return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
        }
    }



    @GetMapping("/fpt-dorm/admin/edit-room/{dormId}/{floorId}/{roomId}")
    public String showEditRoomForm(@PathVariable int dormId, @PathVariable int floorId, @PathVariable int roomId, Model model) {
        Room room = roomService.getRoomById(roomId);
        if (room != null) {
            model.addAttribute("room", room);
            model.addAttribute("dormId", dormId);
            model.addAttribute("floorId", floorId);
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            model.addAttribute("rooms", rooms);
            String dormName = dormService.getDormNameById(dormId);
            model.addAttribute("dormName", dormName);

            Floor floor = floorService.getFloorById(floorId);
            model.addAttribute("floorNumber", floor.getFloorNumber());

            return "admin/dorm-manager/admin_edit_room";
        } else {
            model.addAttribute("error", "Room not found.");
            return "admin/dorm-manager/admin_list_rooms"; // Hoặc trang lỗi khác
        }
    }

    @PostMapping("/fpt-dorm/admin/edit-room/{dormId}/{floorId}/{roomId}")
    public String editRoom(@PathVariable int dormId, @PathVariable int floorId, @PathVariable int roomId, @ModelAttribute Room room, Model model, RedirectAttributes redirectAttributes) {
        Room existingRoom = roomService.getRoomById(roomId);
        if (existingRoom != null) {
            // Check if room number is changed and if the new room number already exists on the floor
            if (!existingRoom.getRoomNumber().equals(room.getRoomNumber())) {
                boolean roomExists = roomService.existsByRoomNumberAndFloorId(room.getRoomNumber(), floorId);
                if (roomExists) {
                    redirectAttributes.addFlashAttribute("error", "Room number already exists in the given floor");
                    return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
                }
            }

            // Update room details
            existingRoom.setRoomNumber(room.getRoomNumber());
            existingRoom.setCapacity(room.getCapacity());
            existingRoom.setPricePerBed(room.getPricePerBed());
            roomService.updateRoom(existingRoom);

            return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
        } else {
            model.addAttribute("error", "Room not found.");
            return "admin/dorm-manager/admin_edit_room"; // Or redirect to an error page
        }
    }




    @GetMapping("/fpt-dorm/admin/delete-room/{dormId}/{floorId}/{roomId}")
    public String deleteRoom(@PathVariable("dormId") int dormId, @PathVariable("floorId") int floorId, @PathVariable("roomId") int roomId, Model model) {
        try {
            roomService.deleteRoom(roomId);
            return "redirect:/fpt-dorm/admin/view-rooms/" + dormId + "/" + floorId;
        } catch (RuntimeException e) {
            model.addAttribute("error", "In a room with a bed in use, you cannot delete it.");
            Floor floor = floorService.getFloorById(floorId);
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            model.addAttribute("floor", floor);
            model.addAttribute("rooms", rooms);
            model.addAttribute("dormId", dormId);
            String dormName = dormService.getDormNameById(dormId);
            model.addAttribute("dormName", dormName);
            // Sắp xếp danh sách phòng theo roomName (tên phòng)
            rooms.sort(Comparator.comparing(Room::getRoomNumber));
            return "admin/dorm-manager/admin_list_rooms";
        }

    }


    @GetMapping("/fpt-dorm/admin/list-beds/{roomId}")
    public String listBeds(Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        // Create a PageRequest with sorting by bedName in ascending order
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("roomNumber", room.getRoomNumber());
        model.addAttribute("roomId", roomId);
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages

        return "admin/dorm-manager/admin_list_beds";
    }


    @GetMapping("/fpt-dorm/admin/add-bed/{roomId}")
    public String showAddBedForm(Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        model.addAttribute("roomId", roomId);
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("roomNumber", room.getRoomNumber());
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages
        model.addAttribute("rooms", roomService.getAllRooms()); // Get all rooms to populate dropdown
        model.addAttribute("bed", new Bed()); // BedForm is a DTO for form submission
        return "admin/dorm-manager/add-bed";
    }

    @PostMapping("/fpt-dorm/admin/add-bed/{roomId}")
    public String addBedToRoom(@ModelAttribute("bed") Bed bed,
                               RedirectAttributes redirectAttributes, @PathVariable int roomId) {
        try {
            bedService.addBedToRoom(roomId, bed.getBedName());
            redirectAttributes.addFlashAttribute("successMessage", "Bed added successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fpt-dorm/admin/list-beds/" + roomId;
    }


    @GetMapping("/fpt-dorm/admin/edit-bed/{roomId}/{bedId}")
    public String showEditBedForm(@PathVariable("bedId") int bedId, Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        model.addAttribute("roomId", roomId);
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("roomNumber", room.getRoomNumber());
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages
        Bed bed = bedService.getBedById(bedId);
        model.addAttribute("bed", bed);
        model.addAttribute("rooms", roomService.getAllRooms()); // Get all rooms to populate dropdown
        return "admin/dorm-manager/edit-bed";
    }

    @PostMapping("/fpt-dorm/admin/edit-bed")
    public String updateBed(@ModelAttribute("bed") Bed bed,
                            RedirectAttributes redirectAttributes) {
        try {
            Bed existingBed = bedService.getBedByName(bed.getBedName());
            if (existingBed != null && existingBed.getBedId() != bed.getBedId()) {
                throw new IllegalArgumentException("Bed name '" + bed.getBedName() + "' already exists.");
            }

            bedService.updateBed(bed);
            redirectAttributes.addFlashAttribute("successMessage", "Bed updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fpt-dorm/admin/list-beds/" + bed.getRoom().getRoomId();
    }


    @GetMapping("/fpt-dorm/admin/delete-bed/{roomId}/{bedId}")
    public String deleteBed(@PathVariable("bedId") int bedId,
                            @PathVariable("roomId") int roomId,
                            RedirectAttributes redirectAttributes) {
        try {
            bedService.deleteBed(bedId);
            redirectAttributes.addFlashAttribute("successMessage", "Bed deleted successfully");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete bed because thí bed is booking.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete bed");
        }
        return "redirect:/fpt-dorm/admin/list-beds/" + roomId;
    }


    @GetMapping("/fpt-dorm/employee/view-dorm-list")
    public String employeeListDorms(Model model) {
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        Collections.sort(dormBedInfoList, (d1, d2) -> d1.getDormName().compareTo(d2.getDormName()));
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        Dorm dorm = new Dorm();
        model.addAttribute("dorm", dorm);
        return "employee/dorm-manager/dorm_list"; // Thymeleaf view name
    }


    @PostMapping("/fpt-dorm/employee/save")
    public String employeeSaveDorm(@ModelAttribute("dorm") Dorm dorm, Model model) {
        try {
            dormService.saveDorm(dorm);
            return "redirect:/fpt-dorm/employee/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "employee/dorm-manager/dorm_list";
        }
    }

    @GetMapping("/fpt-dorm/employee/delete-dorm/{dormId}")
    public String employeeDeleteDorm(@PathVariable("dormId") int dormId, Model model) {
        try {
            dormService.deleteDorm(dormId);
            return "redirect:/fpt-dorm/employee/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", "In a dorm room that is in use, you cannot delete it.");
            // Trả về lại view admin_dorm_list để hiển thị lỗi
            List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
            model.addAttribute("dormBedInfoList", dormBedInfoList);
            Dorm dorm = new Dorm();
            model.addAttribute("dorm", dorm);
            return "employee/dorm-manager/dorm_list";
        }
    }


    @GetMapping("/fpt-dorm/employee/edit-dorm-form/{dormId}")
    public String UpdateDorm(@PathVariable("dormId") int dormId, Model model) {
        Dorm dorm = dormService.getDormById(dormId);
        model.addAttribute("dorm", dorm);
        List<DormBedInfoDTO> dormBedInfoList = dormService.getAllDormBedInfo();
        Collections.sort(dormBedInfoList, (d1, d2) -> d1.getDormName().compareTo(d2.getDormName()));
        model.addAttribute("dormBedInfoList", dormBedInfoList);
        return "employee/dorm-manager/edit_dorm"; // Thymeleaf view name
    }

    @PostMapping("/fpt-dorm/employee/update-dorm")
    public String employeeUpdateDorm(@ModelAttribute("dorm") Dorm dorm, Model model) {
        try {
            dormService.updateDorm(dorm);
            return "redirect:/fpt-dorm/employee/view-dorm-list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update dorm.");
            return "employee/dorm-manager/edit_dorm";
        }
    }


    @GetMapping("/fpt-dorm/employee/view-floor-list/{dormId}")
    public String employeeFloorList(@PathVariable("dormId") int dormId, Model model) {
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);
        model.addAttribute("dormId", dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        return "employee/dorm-manager/floor_list";
    }

    @GetMapping("/fpt-dorm/employee/add-floor-form/{dormId}")
    public String employeeAddFloor(@PathVariable int dormId) {
        floorService.addFloor(dormId);
        return "redirect:/fpt-dorm/employee/view-floor-list/{dormId}";
    }


    @GetMapping("/fpt-dorm/employee/delete-floor/{floorId}")
    public String DeleteFloor(@PathVariable("floorId") int floorId, RedirectAttributes redirectAttributes) {
        // Lấy thông tin tầng cần xóa
        Floor floor = floorService.getFloorById(floorId);
        if (floor == null) {
            // Tầng không tồn tại
            redirectAttributes.addFlashAttribute("error", "Floor not found.");
            return "redirect:/fpt-dorm/admin/view-floor-list";
        }

        // Lấy tầng cao nhất trong cùng một ký túc xá
        Floor highestFloor = floorService.getHighestFloorByDormId(floor.getDorm().getDormId());

        // Kiểm tra nếu tầng cần xóa có phải là tầng cao nhất không
        if (floor.getFloorNumber().equals(highestFloor.getFloorNumber())) {
            // Kiểm tra nếu tầng có phòng nào không
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            if (rooms != null && !rooms.isEmpty()) {
                // Tầng có chứa phòng, không cho phép xóa
                redirectAttributes.addFlashAttribute("error", "The floor cannot be deleted because it contains rooms.");
            } else {
                // Xóa tầng nếu không có phòng
                floorService.deleteFloor(floorId);
            }
        } else {
            // Tầng không phải là tầng cao nhất, không cho phép xóa
            redirectAttributes.addFlashAttribute("error", "Only the highest floor can be deleted.");
        }

        // Chuyển hướng về danh sách tầng
        return "redirect:/fpt-dorm/admin/view-floor-list/" + floor.getDorm().getDormId();
    }



    @GetMapping("/fpt-dorm/employee/edit-floor-form/{dormId}/{floorId}")
    public String EditFloor(@PathVariable("dormId") int dormId,
                            @PathVariable("floorId") int floorId,
                            Model model) {
        Floor floor = floorService.getFloorById(floorId);
        List<FloorBedUsage> floorBedUsageList = floorService.getFloorBedUsageByDormId(dormId);
        model.addAttribute("dormId", dormId);
        model.addAttribute("floorBedUsageList", floorBedUsageList);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);
        if (floor != null) {
            model.addAttribute("floor", floor);
            return "employee/dorm-manager/edit_floor";
        } else {
            model.addAttribute("error", "Floor not found.");
            return "employee/dorm-manager/floor_list";
        }
    }

    @PostMapping("/fpt-dorm/employee/update-floor")
    public String employeeUpdateFloor(@ModelAttribute("floor") Floor floor, Model model) {
        try {
            if (floor.getFloorNumber() < 0) {
                model.addAttribute("error", "Floor number cannot be negative.");
                return "employee/dorm-manager/edit_floor";
            }

            if (floorService.isFloorNumberDuplicateInDorm(floor.getDorm().getDormId(), floor.getFloorNumber(), floor.getFloorId())) {
                model.addAttribute("error", "Floor number already exists for this dorm.");
                return "employee/dorm-manager/edit_floor";
            }

            floorService.updateFloor(floor);

            // Retrieve updated floor list after update
            List<Floor> updatedFloorList = floorService.getFloorsByDormId(floor.getDorm().getDormId());
            model.addAttribute("dormId", floor.getDorm().getDormId());
            model.addAttribute("floorList", updatedFloorList);

            return "redirect:/fpt-dorm/employee/view-floor-list/" + floor.getDorm().getDormId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update floor.");
            return "employee/dorm-manager/edit_floor";
        }
    }


    @GetMapping("/fpt-dorm/employee/view-rooms/{dormId}/{floorId}")
    public String listRooms(@PathVariable int dormId, @PathVariable int floorId, Model model) {
        try {
            Floor floor = floorService.getFloorById(floorId);
            if (floor != null) {
                List<Room> rooms = roomService.getRoomsByFloorId(floorId);

                // Sắp xếp danh sách phòng theo roomName (tên phòng)
                rooms.sort(Comparator.comparing(Room::getRoomNumber));

                model.addAttribute("floor", floor);
                model.addAttribute("rooms", rooms);
                model.addAttribute("dormId", dormId);
                String dormName = dormService.getDormNameById(dormId);
                model.addAttribute("dormName", dormName);
                return "employee/dorm-manager/room_list"; // Trang view_rooms để hiển thị danh sách các phòng
            } else {
                model.addAttribute("error", "Floor not found.");
                return "employee/dorm-manager/floor_list";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to view rooms: " + e.getMessage());
            return "employee/dorm-manager/floor_list";
        }
    }


    @GetMapping("/fpt-dorm/employee/add-room/{dormId}/{floorId}")
    public String addRoom(@PathVariable int dormId, @PathVariable int floorId, Model model) {
        model.addAttribute("floorId", floorId);
        model.addAttribute("dormId", dormId);
        model.addAttribute("room", new Room());
        List<Room> rooms = roomService.getRoomsByFloorId(floorId);
        // Sắp xếp danh sách phòng theo roomName (tên phòng)
        rooms.sort(Comparator.comparing(Room::getRoomNumber));
        model.addAttribute("rooms", rooms);
        Floor floor = floorService.getFloorById(floorId);
        model.addAttribute("floor", floor);
        String dormName = dormService.getDormNameById(dormId);
        model.addAttribute("dormName", dormName);

        try {
            model.addAttribute("floorNumber", floor.getFloorNumber());

        } catch (RuntimeException e) {
            model.addAttribute("error", "Fail to add room");

            return "employee/dorm-manager/add_room"; // Hoặc trang lỗi khác
        }

        return "employee/dorm-manager/add_room";
    }


    @PostMapping("/fpt-dorm/employee/add-room/{dormId}/{floorId}")
    public String addRoom(@PathVariable int dormId, @PathVariable int floorId, @ModelAttribute Room room, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Validate room capacity
            if (room.getCapacity() < 0) {
                redirectAttributes.addFlashAttribute("error", "Capacity cannot be less than 0");
                return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
            }

            // Validate room price per bed
            if (room.getPricePerBed() < 0) {
                redirectAttributes.addFlashAttribute("error", "Price per bed cannot be less than 0");
                return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
            }

            // Validate if the room number already exists in the given floor
            boolean roomExists = roomService.existsByRoomNumberAndFloorId(room.getRoomNumber(), floorId);
            if (roomExists) {
                redirectAttributes.addFlashAttribute("error", "Room number already exists in the given floor");
                return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
            }

            // Add room to floor
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            if (rooms.size() >= 10) {
                redirectAttributes.addFlashAttribute("error", "The floor already has 10 rooms. Cannot add more.");
                return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
            }
            roomService.addRoomToFloor(floorId, room);

            // Create and add beds to the room based on the capacity
            for (int i = 1; i <= room.getCapacity(); i++) {
                String bedName = room.getRoomNumber() + "-" + i; // Create bed names like A101-1, A101-2, ...
                bedService.addBedToRoom(room.getRoomId(), bedName);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Room and beds added successfully");
            return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Fail to add room");
            return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
        }
    }


    @GetMapping("/fpt-dorm/employee/edit-room/{dormId}/{floorId}/{roomId}")
    public String editRoom(@PathVariable int dormId, @PathVariable int floorId, @PathVariable int roomId, Model model) {
        Room room = roomService.getRoomById(roomId);
        if (room != null) {
            model.addAttribute("room", room);
            model.addAttribute("dormId", dormId);
            model.addAttribute("floorId", floorId);
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            model.addAttribute("rooms", rooms);
            String dormName = dormService.getDormNameById(dormId);
            model.addAttribute("dormName", dormName);

            Floor floor = floorService.getFloorById(floorId);
            model.addAttribute("floorNumber", floor.getFloorNumber());

            return "employee/dorm-manager/edit_room";
        } else {
            model.addAttribute("error", "Room not found.");
            return "employee/dorm-manager/edit_room"; // Hoặc trang lỗi khác
        }
    }

    @PostMapping("/fpt-dorm/employee/edit-room/{dormId}/{floorId}/{roomId}")
    public String EditRoom(@PathVariable int dormId, @PathVariable int floorId, @PathVariable int roomId, @ModelAttribute Room room, Model model) {
        Room existingRoom = roomService.getRoomById(roomId);
        if (existingRoom != null) {
            try {
                // Validate unique room number within the same floor
                Room duplicateRoom = roomService.findByFloorAndRoomNumber(existingRoom.getFloor(), room.getRoomNumber());
                if (duplicateRoom != null && !duplicateRoom.getRoomId().equals(roomId)) {
                    throw new IllegalArgumentException("Room number already exists on this floor.");
                }

                // Validate non-negative capacity
                if (room.getCapacity() < 0) {
                    throw new IllegalArgumentException("Capacity cannot be negative.");
                }

                // Validate price per bed greater than zero
                if (room.getPricePerBed() <= 0) {
                    throw new IllegalArgumentException("Price per bed must be greater than zero.");
                }

                existingRoom.setRoomNumber(room.getRoomNumber());
                existingRoom.setCapacity(room.getCapacity());
                existingRoom.setPricePerBed(room.getPricePerBed());
                roomService.updateRoom(existingRoom);

                return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Fail to edit room");
                model.addAttribute("room", existingRoom); // Retain the existing room details
                model.addAttribute("dormId", dormId);
                model.addAttribute("floorId", floorId);
                model.addAttribute("dormName", dormService.getDormNameById(dormId));
                model.addAttribute("floorNumber", floorService.getFloorById(floorId).getFloorNumber());
                return "employee/dorm-manager/edit_room";
            }
        } else {
            model.addAttribute("error", "Room not found.");
            return "employee/dorm-manager/edit_room"; // Or another error page
        }
    }


    @GetMapping("/fpt-dorm/employee/delete-room/{dormId}/{floorId}/{roomId}")
    public String DeleteRoom(@PathVariable("dormId") int dormId, @PathVariable("floorId") int floorId, @PathVariable("roomId") int roomId, Model model) {
        try {
            roomService.deleteRoom(roomId);
            return "redirect:/fpt-dorm/employee/view-rooms/" + dormId + "/" + floorId;
        } catch (RuntimeException e) {
            model.addAttribute("error", "In a room with a bed in use, you cannot delete it.");
            Floor floor = floorService.getFloorById(floorId);
            List<Room> rooms = roomService.getRoomsByFloorId(floorId);
            model.addAttribute("floor", floor);
            model.addAttribute("rooms", rooms);
            model.addAttribute("dormId", dormId);
            String dormName = dormService.getDormNameById(dormId);
            model.addAttribute("dormName", dormName);
            // Sắp xếp danh sách phòng theo roomName (tên phòng)
            rooms.sort(Comparator.comparing(Room::getRoomNumber));
            return "employee/dorm-manager/room_list";
        }

    }


    @GetMapping("/fpt-dorm/employee/list-beds/{roomId}")
    public String ListBeds(Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        // Create a PageRequest with sorting by bedName in ascending order
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("roomNumber", room.getRoomNumber());
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages

        return "employee/dorm-manager/bed_list";
    }


    @GetMapping("/fpt-dorm/employee/add-bed/{roomId}")
    public String AddBedForm(Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        model.addAttribute("roomId", roomId);
        Room room = roomService.getRoomById(roomId);
        model.addAttribute("roomNumber", room.getRoomNumber());
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages
        model.addAttribute("rooms", roomService.getAllRooms()); // Get all rooms to populate dropdown
        model.addAttribute("bed", new Bed()); // BedForm is a DTO for form submission// BedForm is a DTO for form submission
        return "employee/dorm-manager/add_bed";
    }

    @PostMapping("/fpt-dorm/employee/add-bed/{roomId}")
    public String BedToRoom(@ModelAttribute("bed") Bed bed, RedirectAttributes redirectAttributes, @PathVariable int roomId) {
        try {
            bedService.addBedToRoom(roomId, bed.getBedName());
            redirectAttributes.addFlashAttribute("successMessage", "Bed added successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/fpt-dorm/employee/list-beds/" + roomId;
    }



    @GetMapping("/fpt-dorm/employee/edit-bed/{roomId}/{bedId}")
    public String EditBedForm(@PathVariable("bedId") int bedId, Model model, @RequestParam(defaultValue = "0") int page, @PathVariable int roomId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "bedName"));

        // Fetch the paginated and sorted list of beds
        Page<Bed> bedsPage = bedRepository.findByRoomRoomId(roomId, pageRequest);
        model.addAttribute("roomId", roomId);
        model.addAttribute("beds", bedsPage);
        model.addAttribute("currentPage", page); // Current page number
        model.addAttribute("totalPages", bedsPage.getTotalPages()); // Total number of pages
        Bed bed = bedService.getBedById(bedId);
        model.addAttribute("bed", bed);
        model.addAttribute("rooms", roomService.getAllRooms()); // Get all rooms to populate dropdown
        return "employee/dorm-manager/edit_bed";
    }

    @PostMapping("/fpt-dorm/employee/edit-bed")
    public String editBed(@ModelAttribute Bed bed, Model model) {
        Room room = roomService.getRoomById(bed.getRoom().getRoomId());

        if (room == null) {
            model.addAttribute("errorMessage", "Room not found.");
            model.addAttribute("bed", bed);
            model.addAttribute("rooms", roomService.getAllRooms());
            return "employee/dorm-manager/edit_bed";
        }

        // Validate unique bed name within the same room
        if (roomService.isBedNameDuplicate(bed.getBedName(), room) && !bedService.getBedById(bed.getBedId()).getBedName().equals(bed.getBedName())) {
            model.addAttribute("errorMessage", "Bed name already exists in the selected room.");
            model.addAttribute("bed", bed);
            model.addAttribute("rooms", roomService.getAllRooms());
            return "employee/dorm-manager/edit_bed";
        }

        try {
            bedService.updateBed(bed);
            model.addAttribute("successMessage", "Bed updated successfully.");
            return "redirect:/fpt-dorm/employee/list-beds/" + bed.getRoom().getRoomId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update bed.");
            model.addAttribute("bed", bed);
            model.addAttribute("rooms", roomService.getAllRooms());
            return "employee/dorm-manager/edit_bed";
        }
    }


    @GetMapping("/fpt-dorm/employee/delete-bed/{roomId}/{bedId}")
    public String DeleteBed(@PathVariable("bedId") int bedId,
                            @PathVariable("roomId") int roomId,
                            RedirectAttributes redirectAttributes) {
        try {
            bedService.deleteBed(bedId);
            redirectAttributes.addFlashAttribute("successMessage", "Bed deleted successfully");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete bed because this bed is booking.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete bed");
        }
        return "redirect:/fpt-dorm/employee/list-beds/" + roomId;
    }

}
