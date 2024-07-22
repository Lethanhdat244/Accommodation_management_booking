package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.entity.Semester;
import com.accommodation_management_booking.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SemesterController {

    @Autowired
    private SemesterRepository semesterRepository;

    @GetMapping("/fpt-dorm/user/booking/semesters")
    @ResponseBody
    public List<Semester> getSemesters() {
        return semesterRepository.findAll();
    }
}