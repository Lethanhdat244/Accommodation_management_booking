package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.UserDTO;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user-manager")
public class UserManagerController {
    @Autowired
    private UserRepository repo;

    @GetMapping({"", "/"})
    public String showUserList(Model model) {
        List<User> users = repo.findAllByRole(User.Role.USER);
        model.addAttribute("users", users);
        return "user-manager/index";
    }

    @GetMapping({"/employees"})
    public String showEmployeeList(Model model) {
        List<User> users = repo.findAllByRole(User.Role.EMPLOYEE);
        model.addAttribute("users", users);
        return "user-manager/index";
    }

    @GetMapping({"/search"})
    public String searchUserList(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "category", required = false) String category) {
        List<User> users = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            users = repo.findAll();
        } else if (category.isEmpty()) {
            users = repo.searchAllBy(keyword);
        } else {
            switch (category) {
                case "ID":
                    try {
                        int id = Integer.parseInt(keyword);
                        users.add(repo.findById(id).orElse(null));
                    } catch (NumberFormatException e) {
                        users = null;
                    }
                    break;
                case "Name": users = repo.searchByName(keyword); break;
                case "Email": users = repo.searchByEmail(keyword); break;
                case "Phone": users = repo.searchByPhoneNumber(keyword); break;
            }
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        return "user-manager/index";
    }

    @GetMapping("/edit")
    public String showEditPage (Model model, @RequestParam int id) {
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
            return "redirect:/user-manager";
        }
        return "user-manager/edit-user";
    }


}
