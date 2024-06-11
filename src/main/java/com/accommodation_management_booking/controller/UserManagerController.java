package com.accommodation_management_booking.controller;

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

@Controller
@AllArgsConstructor
public class UserManagerController {
    @Autowired
    private UserRepository repo;

    private final UserService userService;

    @GetMapping("/fpt-dorm/admin/student/all-student")
    public String showStudentList(Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "2") int size,
                                  @RequestParam(defaultValue = "userId,asc") String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<User> userPage = userService.findAllUser(pageable);
        model.addAttribute("userPage", userPage);
        model.addAttribute("sort", sort);
        return "admin/student-manager/all_student";
    }

    @GetMapping("/fpt-dorm/admin/student/search")
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

        if (keyword == null || keyword.isEmpty()) {
            userPage = userService.findAllUser(pageable);
        } else if (category == null || category.isEmpty()) {
            userPage = userService.findAllUser(pageable);
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
                    userPage = userService.searchByName(keyword, pageable);
                    break;
                case "Email":
                    userPage = userService.searchByEmail(keyword, pageable);
                    break;
                case "Phone":
                    userPage = userService.searchByPhoneNumber(keyword, pageable);
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
        return "admin/student-manager/all_student";
    }

    @GetMapping("/fpt-dorm/admin/student/edit/id={id}")
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
            return "redirect:/fpt-dorm/admin/student/all-student";
        }
        return "admin/student-manager/edit_student";
    }

    //test controller
    public void test(@RequestParam("userId") int id) {
        System.out.println("Received param: " + id);
    }

    @PostMapping("/fpt-dorm/admin/student/edit/save")
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
            return "admin/student-manager/edit_student";
        }
        return "redirect:/fpt-dorm/admin/student/edit/id=" + id + "?success";
    }

    @GetMapping("/fpt-dorm/admin/student/view/id={id}")
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
            return "redirect:/fpt-dorm/admin/student/all-student";
        }
        return "admin/student-manager/view_student";
    }

    @DeleteMapping("/fpt-dorm/admin/student/delete/id={id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fpt-dorm/admin/student/add")
    public String showCreateForm(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO", userDTO);
        return "admin/student-manager/add_student";
    }

    @PostMapping("/fpt-dorm/admin/student/add/submit")
    public String createUser(@ModelAttribute("userDTO") UserDTO userDTO,
                               Model model,
                               @RequestParam("avatar") MultipartFile[] avatars,
                               @RequestParam("frontface") MultipartFile[] frontCccdImages,
                               @RequestParam("backface") MultipartFile[] backCccdImages) {
        try {
            for (User user : repo.findAll()) {
                if (user.getEmail().equals(userDTO.getEmail())) {
                    model.addAttribute("errorMessage", "Email already exists");
                    return "admin/student-manager/add_student";
                }
            }
            userService.saveUser(userDTO, avatars, frontCccdImages, backCccdImages);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while creating user. Please try again.");
            e.printStackTrace();
            return "admin/student-manager/add_student";
        }
        return "redirect:/fpt-dorm/admin/student/add?success";
    }

}
