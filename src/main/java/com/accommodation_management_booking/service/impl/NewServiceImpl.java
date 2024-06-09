package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.dto.NewDTO;
import com.accommodation_management_booking.entity.New;
import com.accommodation_management_booking.repository.NewRepository;
import com.accommodation_management_booking.service.NewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewServiceImpl implements NewService {

    @Autowired
    private NewRepository newsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<NewDTO> getAllNews() {
        return newsRepository.findAll().stream()
                .map(news -> modelMapper.map(news, NewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public NewDTO getNewsById(Long id) {
        return newsRepository.findById(id).map(news -> modelMapper.map(news, NewDTO.class)).orElse(null);
    }

    @Override
    public NewDTO saveNews(NewDTO newsDTO) {
        New news = modelMapper.map(newsDTO, New.class);
        return modelMapper.map(newsRepository.save(news), NewDTO.class);
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public Page<New> getAllNewsByPage(int page, int size) {
        return newsRepository.findAllByOrderByNewsIdDesc(PageRequest.of(page, size));
    }
    @Override
    public Page<New> searchNewsByTitle(String keyword, int page, int size) {
        return newsRepository.findByTitleContainingIgnoreCaseOrderByNewsIdDesc(keyword, PageRequest.of(page, size));
    }
}
