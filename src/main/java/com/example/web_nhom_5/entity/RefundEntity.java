package com.example.web_nhom_5.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "refund")
@Data
public class RefundEntity {
    @Id
    private long id;

    @Column(nullable = false)
    private String bankType;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String bankHolderName;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private boolean completed = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id") // referencedColumnName là không bắt buộc nếu cột PK bên kia tên là 'id'
    @MapsId // Rất quan trọng: Ánh xạ giá trị PK của RefundEntity từ PK của BookingRoomEntity
    private BookingRoomEntity booking; // Bản ghi Refund này liên kết với DUY NHẤT một BookingRoomEntity
}
