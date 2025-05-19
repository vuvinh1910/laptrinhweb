package com.example.web_nhom_5.entity;

import com.example.web_nhom_5.service.implement.UserVoucherService;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ForgotPassword> forgotPassword;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<BookingRoomEntity> bookingRooms;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserVoucherEntity> userVouchers;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

}
