package com.example.web_nhom_5.service;

import com.example.web_nhom_5.dto.request.RoomCreateRequest;
import com.example.web_nhom_5.dto.request.RoomUpdateRequest;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.entity.RoomEntity;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    RoomResponse addRoom(RoomCreateRequest room);
    RoomEntity getRoomById(Long roomId);
    List<RoomResponse> getAllRooms();
    RoomResponse updateRoom(RoomUpdateRequest room, Long roomId);
    void deleteRoom(Long roomId);
    List<RoomResponse> getAllRoomsByHotel(Long hotel_id);
    List<RoomResponse> getAllRoomsByLocationCodeAndHotelId(String locationCode, Long hotelId);
}
