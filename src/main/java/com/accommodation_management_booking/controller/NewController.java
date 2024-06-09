package com.accommodation_management_booking.controller;

import com.accommodation_management_booking.dto.NewDTO;
import com.accommodation_management_booking.entity.New;
import com.accommodation_management_booking.repository.NewRepository;
import com.accommodation_management_booking.service.NewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NewController {

    @Autowired
    private NewRepository newRepository;

    @Autowired
    private NewService newService;

//    @GetMapping("/fpt-dorm/user/news")
//    public String getNews(Model model) {
//        List<NewDTO> newDTOList=newService.getAllNews();
//        model.addAttribute("newDTOList",newDTOList);
//        return "new";
//    }

    @GetMapping("fpt-dorm/user/news")
    public String newfeed(Model model, Authentication authentication) {
        List<New> newList=newRepository.findTop10ByOrderByNewsIdDesc();
        model.addAttribute("newDTOList",newList);
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
        return "new";
    }

    @GetMapping("/fpt-dorm/employee/view-news")
    public String viewNews(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @RequestParam(required = false) String keyword) {
        Page<New> newsPage;
        if (keyword == null || keyword.isEmpty()) {
            newsPage = newService.getAllNewsByPage(page, size);
        } else {
            newsPage = newService.searchNewsByTitle(keyword, page, size);
            model.addAttribute("keyword", keyword);
        }
        model.addAttribute("newsPage", newsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "employee/view_news";
    }

    @GetMapping("/fpt-dorm/employee/news/detail/newsId={id}")
    public String newsDetail(Model model, @PathVariable("id") Long id) {
        NewDTO news = newService.getNewsById(id);
        model.addAttribute("news", news);
        return "employee/detail_new";
    }

}
