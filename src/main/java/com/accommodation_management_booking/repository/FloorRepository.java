package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer> {
    List<Floor> findByDormDormId(Integer dormId);

}