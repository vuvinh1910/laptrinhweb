package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    List<HotelEntity> findAllByLocation_LocationCode(String locationCode);

    @Query("select r from HotelEntity r where " +
            "(:locationCode is null or r.location.locationCode = :locationCode) and " +
            "(:star is null or r.star >= :star) and " +
            "(:rate is null or r.rate >= :rate) and " +
            "(:price is null or r.price <= :price)"
    )
    List<HotelEntity> hotelFilter(String locationCode, Integer star, Double rate, Long price);

    @Modifying
    @Query("UPDATE HotelEntity h SET h.price = (" +
            "SELECT MIN(r.roomPrice) FROM RoomEntity r WHERE r.hotel.id = h.id)")
    void updateAllHotelMinPrices();
}
