package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.ResidentHistoryDTO;

import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.ResidentHistoryService;
import com.accommodation_management_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class ResidentHistoryController {

    private final ResidentHistoryService residentHistoryService;
    @Autowired
    private UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public ResidentHistoryController(ResidentHistoryService residentHistoryService, UserService userService) {
        this.residentHistoryService = residentHistoryService;
        this.userService = userService;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = oauth2User.getAttribute("email");
            return userRepository.searchUserByEmail(email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.searchUserByEmail(userDetails.getUsername());
        } else {
            return null;
        }
    }

    @GetMapping("/fpt-dorm/user/resident-history/list")
    public String getResidentHistoryByUserId(Model model, Authentication authentication, @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            User user = getUserFromAuthentication(authentication);
            model.addAttribute("email", user.getEmail());

            Pageable pageable = PageRequest.of(page, 2);
            Page<ResidentHistoryDTO> residentHistoryPage = residentHistoryService.findAllByUserIdOrderByCheckOutDateDesc(user.getUserId(), pageable);

            model.addAttribute("residentHistoryPage", residentHistoryPage);

            return "/user/resident_history";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while retrieving the user's residential history data.");
            return "error-page";
        }
    }

//
//    @GetMapping("/fpt-dorm/user/search-by-room")
//    public String searchByRoomNumber(@RequestParam("roomNumber") String roomNumber,
//                                     @RequestParam(value = "page", defaultValue = "0") int page,
//                                     @RequestParam(value = "size", defaultValue = "2") int size,
//                                     Model model, Authentication authentication) {
//        User user = getUserFromAuthentication(authentication);
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ResidentHistoryDTO> residentHistoryPage = residentHistoryService.searchByRoomNumber(roomNumber, pageable);
//
//        model.addAttribute("residentHistoryPage", residentHistoryPage);
//
//
//        if (residentHistoryPage.isEmpty()) {
//            model.addAttribute("message", "No room information found " + roomNumber);
//        }
//        return "/user/search_residentHistory";
//    }
@GetMapping("/fpt-dorm/user/search-by-room")
public String searchByRoomNumber(@RequestParam("roomNumber") String roomNumber,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "2") int size,
                                 Model model, Authentication authentication) {
    User user = getUserFromAuthentication(authentication);
    if (user == null) {
        model.addAttribute("message", "User not found.");
        return "/user/search_residentHistory";
    }
    int userId = user.getUserId();
    Pageable pageable = PageRequest.of(page, size);
    Page<ResidentHistoryDTO> residentHistoryPage = residentHistoryService.searchByRoomNumber(roomNumber, userId, pageable);

    model.addAttribute("residentHistoryPage", residentHistoryPage);

    if (residentHistoryPage.isEmpty()) {
        model.addAttribute("message", "No room information found for " + roomNumber);
    }
    return "/user/search_residentHistory";
}






    //----------------------------------------------

    @GetMapping("/fpt-dorm/{role}/Resident_History/list")
    public String getUsersResidentHistory(
            @PathVariable("role") String role,
            Pageable pageable,
            Model model) {
        Pageable pageableRequest = PageRequest.of(pageable.getPageNumber(), 2);
        Page<ResidentHistoryDTO> residentHistoryPage = residentHistoryService.getUsersResidentHistory(pageableRequest);

        for (ResidentHistoryDTO dto : residentHistoryPage.getContent()) {
            System.out.println("Email: " + dto.getEmail());
        }
        model.addAttribute("residentHistoryPage", residentHistoryPage);
        model.addAttribute("role", role);
        if ("admin".equals(role)) {
            return "admin/admin-resident-history";
        } else if ("employee".equals(role)) {
            return "employee/employee_Resident_History";
        } else {
            return "error/404";
        }
    }


    @GetMapping("/fpt-dorm/{role}/Resident_History/search")
    public String searchByUserName(
            @PathVariable("role") String role,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResidentHistoryDTO> residentHistoryPage = residentHistoryService.searchByUserName(keyword, pageable);

        model.addAttribute("residentHistoryPage", residentHistoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);

        if (residentHistoryPage.isEmpty()) {
            model.addAttribute("message", "User name is not in the system");
        }

        if ("admin".equals(role)) {
            return "admin/admin_residentHistoryS";
        } else if ("employee".equals(role)) {
            return "employee/employee_resident_historysearch";
        } else {
            return "error/404";
        }
    }


    @GetMapping("/fpt-dorm/{role}/Resident_History/detail/{userId}")
    public String showResidentDetail(
            @PathVariable("role") String role,
            @PathVariable("userId") int userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<ResidentHistoryDTO> residentDetail = residentHistoryService.findAllByUserIdOrderByCheckOutDateDesc(userId, pageable);
        model.addAttribute("residentDetail", residentDetail);
        model.addAttribute("userId", userId);
        model.addAttribute("role", role);

        if ("admin".equals(role)) {
            return "admin/admin-resident-history-detail";
        } else if ("employee".equals(role)) {
            return "employee/employee-resident-history-detail";
        } else {
            return "error/404";
        }
    }

}





