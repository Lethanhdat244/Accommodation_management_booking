package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "semester")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer semesterId;

    private String semesterName;
    private LocalDate startDate;
    private LocalDate endDate;

}
