package com.example.web_nhom_5.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_voucher")
@Data
public class UserVoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    @JsonBackReference
    private VoucherEntity voucher;

    @Column
    private boolean isUsed;
}
