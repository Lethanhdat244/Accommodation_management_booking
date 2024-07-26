package com.accommodation_management_booking.service.impl;

import com.accommodation_management_booking.config.GenderMapper;
import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.entity.Bed;
import com.accommodation_management_booking.entity.Dorm;
import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.BedRepository;
import com.accommodation_management_booking.repository.DormRepository;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.service.DormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DormServiceImpl implements DormService {

    private final DormRepository dormRepository;
    private final BedRepository bedRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public DormServiceImpl(DormRepository dormRepository, BedRepository bedRepository) {
        this.dormRepository = dormRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public DormBedInfoDTO getDormBedInfo(Integer dormId) {
        Dorm dorm = dormRepository.findById(dormId).orElse(null);
        if (dorm == null) {
            return null;
        }

        List<Bed> beds = bedRepository.findByRoom_Floor_Dorm_DormId(dormId);
        int totalBeds = beds.size();
        int usedBeds = (int) beds.stream().filter(bed -> !bed.getIsAvailable()).count();
        int availableBeds = totalBeds - usedBeds;

        return new DormBedInfoDTO(dorm.getDormId(), dorm.getDormName(), totalBeds, usedBeds, availableBeds);
    }
    private Integer getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userRepository.findByEmail(userDetails.getUsername());
                return user.getUserId(); // Assuming userId is the field name
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String email = oauth2User.getAttribute("email");
                User user = userRepository.findByEmail(email);
                return user.getUserId();
            }
        }
        throw new IllegalStateException("User not found in context");
    }
    public List<DormBedInfoDTO> getAllDormBedInfo1() {
        Integer userId = getLoggedInUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Dorm.DormGender dormGender = GenderMapper.map(user.getGender());
        List<Dorm> dorms = dormRepository.findByDormGender(dormGender);
        return dorms.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private DormBedInfoDTO mapToDTO(Dorm dorm) {
        List<Bed> beds = bedRepository.findByRoom_Floor_Dorm_DormId(dorm.getDormId());
        int totalBeds = beds.size();
        int usedBeds = (int) beds.stream().filter(bed -> !bed.getIsAvailable()).count();
        int availableBeds = totalBeds - usedBeds;
        return new DormBedInfoDTO(dorm.getDormId(), dorm.getDormName(), dorm.getDormGender(), totalBeds, usedBeds, availableBeds);
    }


    @Override
    public List<Dorm> getAllDorms() {
        return dormRepository.findAll();
    }

    @Override
    public Dorm getDormById(int dormId) {
        Optional<Dorm> dormOptional = dormRepository.findById(dormId);
        return dormOptional.orElse(null);
    }

    @Override
    public Dorm saveDorm(Dorm dorm) {
        if (dormRepository.existsByDormName(dorm.getDormName())) {
            throw new IllegalArgumentException("Dorm with this name already exists.");
        }
        return dormRepository.save(dorm);
    }

    @Override
    public void deleteDorm(Integer dormId) {
        dormRepository.deleteById(dormId);
    }

    @Override
    public void updateDorm(Dorm dorm) {
        Dorm existingDorm = dormRepository.findById(dorm.getDormId()).orElseThrow(() -> new IllegalArgumentException("Dorm not found"));

        if (!existingDorm.getDormName().equals(dorm.getDormName()) && dormRepository.existsByDormName(dorm.getDormName())) {
            throw new IllegalArgumentException("Dorm with the same name already exists");
        }

        dormRepository.save(dorm);
    }

    public List<DormBedInfoDTO> getAllDormBedInfo() {
        List<Dorm> dorms = dormRepository.findAll();
        return dorms.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    @Override
    public String getDormNameById(int dormId) {
        Optional<Dorm> dormOptional = dormRepository.findById(dormId);
        return dormOptional.map(Dorm::getDormName).orElse("Dorm not found");
    }
}
