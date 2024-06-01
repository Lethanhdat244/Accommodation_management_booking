package com.accommodation_management_booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {
    @GetMapping("fpt-dorm/employee/home")
    public String employee_homepage(){
        return "employee_homepage";
    }
    @GetMapping("fpt-dorm/employee/student")
    public String employee_student(){return "employee_student";}
    @GetMapping("fpt-dorm/employee/complain")
    public String employee_complain(){return "employee_complain";}
}
