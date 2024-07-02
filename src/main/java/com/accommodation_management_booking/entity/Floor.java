package com.accommodation_management_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "floors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer floorId;

    @ManyToOne
    @JoinColumn(name = "dorm_id", nullable = false)
    private Dorm dorm;

    @Column(nullable = false)
    private Integer floorNumber;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();


    //thêm ms cái này, nhưng cái này nó chỉ là quan hệ giữa 2 bảng thôi, ko liên quan
    @OneToMany(mappedBy = "floor")
    private List<Room> rooms;
}
