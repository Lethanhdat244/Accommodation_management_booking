package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.dto.ComplaintDTO;
import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.New;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ComplainRepository extends JpaRepository<Complaint, Long> {
    @Query("SELECT c FROM Complaint c")
    List<Complaint> getAllRequest();

    @Query("SELECT c FROM Complaint c WHERE c.status = :status")
    List<Complaint> findDoneComplaints(@Param("status") Complaint.Status status);

    @Query("SELECT c FROM Complaint c WHERE c.user.userId = :userid ORDER BY c.createdAt desc")
    Page<Complaint> getRequestsByUserId(@Param("userid") int userid, Pageable pageable);

    @Query("SELECT c FROM Complaint c WHERE c.complaintId = :id")
    Complaint getRequestByComplaintId(@Param("id") int id);

    @Query("SELECT c.status AS status, COUNT(c) AS count " +
            "FROM Complaint c " +
            "WHERE YEAR(c.createdAt) = :year AND MONTH(c.createdAt) = :month " +
            "GROUP BY c.status")
    List<Map<String, Long>> countByStatusForMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM Complaint c WHERE MONTH(c.createdAt) = MONTH(:now) AND YEAR(c.createdAt) = YEAR(:now)")
    int countComplaintsInCurrentMonth(LocalDateTime now);
}
