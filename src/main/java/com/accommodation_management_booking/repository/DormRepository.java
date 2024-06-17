package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.dto.DormBedInfoDTO;
import com.accommodation_management_booking.entity.Dorm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DormRepository extends JpaRepository<Dorm, Integer> {
    Optional<Dorm> findByDormName(String dormName);
    boolean existsByDormName(String dormName);
}
