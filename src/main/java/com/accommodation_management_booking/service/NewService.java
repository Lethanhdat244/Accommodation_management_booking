package com.accommodation_management_booking.service;
import com.accommodation_management_booking.dto.NewDTO;
import com.accommodation_management_booking.entity.New;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewService {
    List<NewDTO> getAllNews();
    NewDTO getNewsById(Long id);
    NewDTO saveNews(NewDTO newsDTO);
    void deleteNews(Long id);
    Page<New> getAllNewsByPage(int page, int size);
    Page<New> searchNewsByTitle(String keyword, int page, int size);
}
