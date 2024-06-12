package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.repository.ComplainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import java.util.List;

@Controller
@AllArgsConstructor
public class EmployeeController {
    @Autowired
    private UserRepository repo;

    private final UserService userService;

    @Autowired
    ComplainRepository complainRepository;

    @GetMapping("fpt-dorm/employee/home")
    public String admin_homepage(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("email", email);
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("email", userDetails.getUsername());
        } else {
            // Handle cases where the authentication is not OAuth2
            model.addAttribute("email", "Unknown");
        }
        return "employee/employee_homepage";
    }

    @GetMapping("/fpt-dorm/employee/complain")
    public String employee_complain(Model model) {
        try {
            List<Complaint> complainList = complainRepository.getAllRequest();
            model.addAttribute("complaintDTOList", complainList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "employee/employee_complain";
    }

    //Xu ly Student
    @GetMapping("/fpt-dorm/employee/student/all-student")
    public String showStudentList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "2") int size,
                                  @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage = userService.findAllStudent(pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("sort", sort);
        return "employee/student-manager/all_student";
    }

    @GetMapping("/fpt-dorm/employee/student/search")
    public String searchUserList(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "category", required = false) String category,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "2") int size,
                                 @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage;

        if (category == null || category.isEmpty()) {
            if (keyword == null || keyword.isEmpty()) {
                userPage = userService.findAllStudent(pageable);
            } else {
                userPage = userService.searchAllByStudent(keyword, pageable);
            }
        } else {
            switch (category) {
                case "ID":
                    try {
                        int id = Integer.parseInt(keyword);
                        Optional<User> userOpt = repo.findById(id);
                        if (userOpt.isPresent()) {
                            userPage = new PageImpl<>(List.of(userOpt.get()), pageable, 1);
                        } else {
                            userPage = Page.empty(pageable);
                        }
                    } catch (NumberFormatException e) {
                        userPage = Page.empty(pageable);
                    }
                    break;
                case "Name":
                    userPage = userService.searchByNameStudent(keyword, pageable);
                    break;
                case "Email":
                    userPage = userService.searchByEmailStudent(keyword, pageable);
                    break;
                case "Phone":
                    userPage = userService.searchByPhoneNumberStudent(keyword, pageable);
                    break;
                default:
                    userPage = Page.empty(pageable);
                    break;
            }
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        return "employee/student-manager/all_student";
    }

    @GetMapping("/fpt-dorm/employee/student/edit/id={id}")
    public String showEditPage(Model model, @PathVariable("id") int id) {
        try {
            if (repo.findById(id).isPresent()) {
                User user = repo.findById(id).get();

                model.addAttribute("user", user);

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setPhoneNumber(user.getPhoneNumber());
                userDTO.setGender(user.getGender());
                userDTO.setAddress(user.getAddress());
                userDTO.setBirthdate(user.getBirthdate());
                userDTO.setRoleUser(user.getRoleUser());
                userDTO.setCccdNumber(user.getCccdNumber());
                model.addAttribute("userDTO", userDTO);
            } else throw new Exception();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/employee/student/all-student";
        }
        return "employee/student-manager/edit_student";
    }

    @PostMapping("/fpt-dorm/employee/student/edit/save")
    public String saveEditUser(@ModelAttribute("userDTO") UserDTO userDTO,
                               @RequestParam("userId") int id,
                               Model model,
                               @RequestParam("avatar") MultipartFile[] avatars,
                               @RequestParam("frontface") MultipartFile[] frontCccdImages,
                               @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            userService.updateUser(userDTO, id, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating user. Please try again.");
            e.printStackTrace();
            return "employee/student-manager/edit_student";
        }
        return "redirect:/fpt-dorm/employee/student/edit/id=" + id + "?success";
    }

    @GetMapping("/fpt-dorm/employee/student/view/id={id}")
    public String showViewPage(Model model, @PathVariable("id") int id) {
        try {
            if (repo.findById(id).isPresent()) {
                User user = repo.findById(id).get();

                model.addAttribute("user", user);

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setPhoneNumber(user.getPhoneNumber());
                userDTO.setGender(user.getGender());
                userDTO.setAddress(user.getAddress());
                userDTO.setBirthdate(user.getBirthdate());
                userDTO.setRoleUser(user.getRoleUser());
                userDTO.setCccdNumber(user.getCccdNumber());
                model.addAttribute("userDTO", userDTO);
            } else throw new Exception();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/fpt-dorm/employee/student/all-student";
        }
        return "employee/student-manager/view_student";
    }

    @DeleteMapping("/fpt-dorm/employee/student/delete/id={id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fpt-dorm/employee/student/add")
    public String showCreateForm(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO", userDTO);
        return "employee/student-manager/add_student";
    }

    @PostMapping("/fpt-dorm/employee/student/add/submit")
    public String createUser(@ModelAttribute("userDTO") UserDTO userDTO,
                             Model model,
                             @RequestParam("avatar") MultipartFile[] avatars,
                             @RequestParam("frontface") MultipartFile[] frontCccdImages,
                             @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            for (User user : repo.findAll()) {
                if (user.getEmail().equals(userDTO.getEmail())) {
                    model.addAttribute("errorMessage", "Email already exists");
                    return "employee/student-manager/add_student";
                }
            }
            userDTO.setRoleUser(User.Role.USER);
            userService.saveUser(userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while creating user. Please try again.");
            e.printStackTrace();
            return "employee/student-manager/add_student";
        }
        return "redirect:/fpt-dorm/employee/student/add?success";
    }
}
