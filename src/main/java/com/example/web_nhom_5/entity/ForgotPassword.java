package com.example.web_nhom_5.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer forgotPasswordId;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date expirationTime;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
}
