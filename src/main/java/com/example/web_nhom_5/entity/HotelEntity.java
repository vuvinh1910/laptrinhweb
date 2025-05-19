package com.example.web_nhom_5.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "hotel")
@Getter
@Setter
public class HotelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 1000)
    private String nameHotel;

    @Column
    private String address;

    @Column
    private String phone;

    @Column
    private int star;

    @Column
    private double rate;

    @Column
    private int countTotalBook;

    @Column(length = 9999)
    private String intro;

    @Column(length = 9999)
    private String description;

    @Column
    private int voucher;

    @Column
    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    @JsonBackReference // tranh loi vong lap vo han json, lien ket voi location.
    private LocationEntity location;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // tranh loi vong lap vo han json, giup lien ket giua cac thuc the.
    private List<RoomEntity> rooms;
}
