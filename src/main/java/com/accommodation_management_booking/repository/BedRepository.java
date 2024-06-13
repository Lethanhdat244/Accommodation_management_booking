package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BedRepository extends JpaRepository<Bed, Integer> {
}
