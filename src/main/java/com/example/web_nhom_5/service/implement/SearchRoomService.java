package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.repository.SearchRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchRoomService {

    @Autowired
    private SearchRoomRepository searchRoomRepository;

    public List<RoomEntity> findAvailableRooms(
            String locationName,
            long minPrice,
            long maxPrice,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        return searchRoomRepository.searchAvailableRooms(locationName, minPrice, maxPrice, checkIn, checkOut);
    }
}