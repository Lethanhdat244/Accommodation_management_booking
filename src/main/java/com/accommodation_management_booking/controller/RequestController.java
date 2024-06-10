package com.accommodation_management_booking.controller;

//import com.accommodation_management_booking.repository.ComplainRepository;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.repository.ComplainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RequestController {
    @Autowired
    ComplainRepository complainRepository;
    @GetMapping("fpt-dorm/user/news/my-request")
    public String studentRequest(Model model) {
        try {
            List<Complaint> complainList = complainRepository.getAllRequest();
            model.addAttribute("complaintDTOList", complainList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "student_request";
    }
}
