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
    List<RoomResponse> getAllRoomsByLocationCode(String locationCode);
    List<RoomResponse> filterRooms(String locationName, LocalDate checkIn, LocalDate checkOut, int numOfPeople, Long price, String roomType);

    List<RoomResponse> getLimitedRooms();
    // can them cac ham chuc nang tim kiem,loc theo yeu cau.
    List<RoomResponse> listAll(String keyword);
    List<RoomResponse> findAllBySpecification(String search);
    // loc them đieu kien dia chỉ
    List<RoomResponse> filterBySpecificationAndAddress(String room,String address);
}
