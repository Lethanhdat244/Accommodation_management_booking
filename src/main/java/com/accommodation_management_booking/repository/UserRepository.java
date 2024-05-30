package com.accommodation_management_booking.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    List<User> searchAllBy(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword%)")
    List<User> searchByName(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE (u.email LIKE %:keyword%)")
    List<User> searchByEmail(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE (u.phoneNumber LIKE %:keyword%)")
    List<User> searchByPhoneNumber(@Param("keyword") String keyword);
}
