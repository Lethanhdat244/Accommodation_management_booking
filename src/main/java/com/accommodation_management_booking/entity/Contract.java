package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contract")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Integer contractId;

    @Column(name = "contract_link", nullable = false)
    private String contractLink;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Constructors, getters, setters
}
