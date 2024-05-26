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

import java.util.List;

@Controller
@RequestMapping("/user-manager")
public class UserManagerController {
    @Autowired
    private UserRepository repo;

    @GetMapping({"", "/"})
    public String showUserList(Model model) {
        List<User> users = repo.findAll(Sort.by(Sort.Direction.ASC, "userId"));
        model.addAttribute("users", users);
        return "user-manager/index";
    }

    @GetMapping("/edit")
    public String showEditPage (Model model, @RequestParam int id) {
        try {
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
            userDTO.setActive(user.isActive());
            userDTO.setCccdNumber(user.getCccdNumber());
            model.addAttribute("userDTO", userDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/user-manager";
        }
        return "user-manager/edit-user";
    }
}
