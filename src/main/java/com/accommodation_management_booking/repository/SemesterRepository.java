package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
}
