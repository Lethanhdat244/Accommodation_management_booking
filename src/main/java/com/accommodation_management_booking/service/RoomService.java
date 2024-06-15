package com.accommodation_management_booking.service;//package com.accommodation_management_booking.service;
//
//import com.accommodation_management_booking.dto.RoomDTO;
//import com.accommodation_management_booking.entity.Room;
//import com.accommodation_management_booking.repository.RoomRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.ui.Model;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class RoomService {
//
//    @Autowired
//    private RoomRepository roomRepo;
//
//
//    public List<RoomDTO> getAllRooms() {
//        List<Room> rooms = roomRepo.findAll();
//        return rooms.stream().map(room -> {
//            RoomDTO dto = new RoomDTO();
//            dto.setRoomId(room.getRoomId());
//            dto.setRoomNumber(room.getRoomNumber());
//            dto.setRoomStatus(room.getRoomStatus() != null ? room.getRoomStatus().name() : "UNKNOWN");
//            dto.setCapacity(room.getCapacity());
//            dto.setStatus(room.getStatus() != null ? room.getStatus().name() : "UNKNOWN");
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//
//    public String saveRoom(RoomDTO roomDTO, RedirectAttributes ra) {
//        ra.addFlashAttribute("message", "The Add Room has been saved successfully");
//        Room room = new Room();
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setRoomStatus(Room.RoomStatus.valueOf(roomDTO.getRoomStatus()));
//        room.setCapacity(roomDTO.getCapacity());
//        room.setStatus(Room.Status.valueOf(roomDTO.getStatus()));
//        room.setCreatedAt(LocalDateTime.now());
//
//        roomRepo.save(room);
//        return "redirect:/admin_list_room/list_room";
//    }
//
////    public RoomDTO getRoomDetail(int id, Model model) {
////        Optional<Room> roomOpt = roomRepo.findById(id);
////        if (roomOpt.isPresent()) {
////            Room room = roomOpt.get();
////            RoomDTO roomDTO = new RoomDTO(
////                    room.getRoomId(),
////                    room.getRoomStatus().name(),
////                    room.getRoomNumber(),
////                    room.getCapacity(),
////                    room.getStatus().name(),
////                    room.getCreatedAt(),
////                    room.getUpdatedAt()
////            );
////            return roomDTO;
////        } else {
////            // Xử lý trường hợp không tìm thấy phòng với id tương ứng
////            return null;
////        }
////    }
//public void updateRoom(RoomDTO roomDTO) throws RoomNotFoundException {
//    Optional<Room> roomOpt = roomRepo.findById(roomDTO.getRoomId());
//    if (roomOpt.isPresent()) {
//        Room room = roomOpt.get();
//        room.setRoomStatus(Room.RoomStatus.valueOf(roomDTO.getRoomStatus()));
//        room.setRoomNumber(roomDTO.getRoomNumber());
//        room.setCapacity(roomDTO.getCapacity());
//        room.setStatus(Room.Status.valueOf(roomDTO.getStatus()));
////            room.setCreatedAt(roomDTO.getCreatedAt());
////            room.setUpdatedAt(roomDTO.getUpdatedAt());
//        roomRepo.save(room);
//    } else {
//            throw new RoomNotFoundException("Could not find any room with ID " + roomDTO.getRoomId());
//        }
//    }
//
//    public void deleteRoom(int roomId, RedirectAttributes ra) {
//        Optional<Room> roomOpt = roomRepo.findById(roomId);
//        if (roomOpt.isPresent()) {
//            Room room = roomOpt.get();
//            if (room.getStatus() == Room.Status.Active) {
//                ra.addFlashAttribute("message", "Cannot delete room. This room is currently active.");
//            } else {
//                roomRepo.delete(room);
//                ra.addFlashAttribute("message", "Room has been deleted successfully");
//            }
//        } else {
//            ra.addFlashAttribute("message", "Room not found");
//        }
//    }
//
//
//
//}
