package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotNull
    @Column(nullable = false, length = 50)
    private String username;

    @NotNull
    @Column(nullable = false, length = 255)
    private String password;

    private Integer roleNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthdate;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Email
    @Column(length = 100)
    private String email;

    @Column(length = 12)
    private String cccdNumber;

    @Column(length = 255)
    private String avatar;

    @Column(length = 255)
    private String frontCccdImage;

    @Column(length = 255)
    private String backCccdImage;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Gender {
        Male, Female, Other
    }
}