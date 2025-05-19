package com.example.web_nhom_5.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "voucher")
@Data
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int value;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private int remain;

    @Column
    private String description;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserVoucherEntity> userVouchers;
}
