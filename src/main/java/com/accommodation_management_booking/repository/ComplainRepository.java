package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.dto.ComplaintDTO;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplainRepository extends JpaRepository<Complaint, Integer> {
    @Query("SELECT c FROM Complaint c")
    List<Complaint> getAllRequest();
    @Query("SELECT c FROM Complaint c WHERE c.user.userId = :userid")
    List<Complaint> getRequestsByUserId(@Param("userid") int userid);
}
