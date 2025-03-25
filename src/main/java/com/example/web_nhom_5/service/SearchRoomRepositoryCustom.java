package com.example.web_nhom_5.service;

import com.example.web_nhom_5.entity.RoomEntity;

import java.time.LocalDate;
import java.util.List;

public interface SearchRoomRepositoryCustom {
    List<RoomEntity> searchAvailableRooms(
            String locationName,
            long minPrice,
            long maxPrice,
            LocalDate checkIn,
            LocalDate checkOut
    );

}
