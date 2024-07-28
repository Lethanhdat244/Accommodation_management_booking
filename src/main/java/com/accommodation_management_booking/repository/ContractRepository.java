package com.accommodation_management_booking.repository;

import com.accommodation_management_booking.entity.Complaint;
import com.accommodation_management_booking.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
    @Query("SELECT c FROM Contract c WHERE c.booking.bookingId = :bookingId")
    Contract getContractByBookingId(@Param("bookingId") int bookingId);

    @Query("SELECT c FROM Contract c WHERE c.booking.bookingId = :bookingId")
    Optional<Contract> getOptionalContractByBookingId(@Param("bookingId") int bookingId);
}
