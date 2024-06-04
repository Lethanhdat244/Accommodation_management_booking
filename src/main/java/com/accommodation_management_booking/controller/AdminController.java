package com.accommodation_management_booking.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("fpt-dorm/admin/home")
    public String admin_homepage(){
        return "admin_homepage";
    }

    @GetMapping("fpt-dorm/admin/admin_list_student")
    public String admin_list_student(){
        return "redirect:/user-manager";
    }

    @GetMapping("fpt-dorm/admin/admin_list_employees")
    public String admin_list_employees(){
        return "redirect:/user-manager/employees";
    }

    @GetMapping("fpt-dorm/admin/admin_add_student")
    public String admin_add_student(){
        return "admin_add_student";
    }

    @GetMapping("fpt-dorm/admin/admin_list_room")
    public String admin_list_room(){
        return "admin_list_room";
    }

    @GetMapping("fpt-dorm/admin/admin_payment_list")
    public String admin_payment_list(){
        return "admin_payment_list";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_type_room")
    public String admin_add_new_type_room(){
        return "admin_add_new_type_room";
    }

    @GetMapping("fpt-dorm/admin/admin_add_new_room")
    public String admin_add_new_room(){
        return "admin_add_new_room";
    }

    @GetMapping("fpt-dorm/admin/admin_list_feedback")
    public String admin_list_feedback(){
        return "admin_list_feedback";
    }

    @GetMapping("fpt-dorm/admin/admin_list_complaint")
    public String admin_list_complaint(){
        return "admin_list_complaint";
    }
}
