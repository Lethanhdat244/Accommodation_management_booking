package com.accommodation_management_booking.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.accommodation_management_booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String username);
    boolean existsByEmail(String email);

    List<User> findAllByRoleUser(User.Role role);

    List<User> findByUsernameContainingOrEmailContainingOrPhoneNumberContaining(String username, String email, String phoneNumber);

    @Query("SELECT u FROM User u WHERE (u.email LIKE %:keyword%)")
    List<User> searchByEmail(@Param("keyword") String keyword);

    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByEmailContaining(String email, Pageable pageable);
    Page<User> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);

}
