package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.conventer.RoomMapper;
import com.example.web_nhom_5.dto.request.RoomCreateRequest;
import com.example.web_nhom_5.dto.request.RoomUpdateRequest;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.entity.HotelEntity;
import com.example.web_nhom_5.entity.LocationEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.enums.SearchOperation;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.HotelRepository;
import com.example.web_nhom_5.repository.LocationRepository;
import com.example.web_nhom_5.repository.RoomRepository;
import com.example.web_nhom_5.service.BookingRoomService;
import com.google.common.base.Joiner;

import com.example.web_nhom_5.service.LocationService;
import com.example.web_nhom_5.service.RoomService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private BookingRoomService bookingRoomService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomMapper roomMapper;
    private final EntityManager em;

    @Override
    public RoomResponse addRoom(RoomCreateRequest room) {
        RoomEntity roomEntity = roomMapper.roomCreateDtoToEntity(room);
        HotelEntity hotel = hotelRepository.findById(room.getHotelId())
                .orElseThrow(() -> new WebException(ErrorCode.HOTEL_NOT_FOUND));
        hotel.getRooms().add(roomEntity);
        roomEntity.setHotel(hotel);
        roomRepository.save(roomEntity);
        hotelRepository.save(hotel);
        return roomMapper.roomEntityToRoomResponse(roomEntity);
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }

    @Override
    public RoomResponse updateRoom(RoomUpdateRequest room, Long roomId) {
        // Lấy RoomEntity hiện tại từ roomId
        RoomEntity roomEntity = getRoomById(roomId);

        HotelEntity oldHotel = roomEntity.getHotel();
        oldHotel.getRooms().remove(roomEntity);
        HotelEntity newHotel = hotelRepository.findById(room.getHotelId()).orElseThrow(() -> new WebException(ErrorCode.HOTEL_NOT_FOUND));
        newHotel.getRooms().add(roomEntity);
        roomEntity.setHotel(newHotel);

        // Cập nhật thông tin cho RoomEntity
        roomMapper.updateRoom(roomEntity, room);

        // Lưu lại RoomEntity
        return roomMapper.roomEntityToRoomResponse(roomRepository.save(roomEntity));
    }

    @Override
    public RoomEntity getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new WebException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public void deleteRoom(Long roomId) {
        RoomEntity roomEntity = getRoomById(roomId);
        HotelEntity hotel = hotelRepository.findById(roomEntity.getHotel().getId())
                .orElseThrow(() -> new WebException(ErrorCode.HOTEL_NOT_FOUND));
        hotel.getRooms().remove(roomEntity);
        roomRepository.deleteById(roomId);
    }

    @Override
    public List<RoomResponse> getAllRoomsByHotel(Long hotel_id) {
        List<RoomEntity> roomEntities = roomRepository.findAllByHotel_Id(hotel_id);
        return roomEntities.stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }

    @Override
    public List<RoomResponse> getAllRoomsByLocationCodeAndHotelId(String locationCode, Long hotelId) {
        if(locationCode.isEmpty()) locationCode=null;
        List<RoomEntity> roomEntities = roomRepository.findAllByHotel_Location_LocationCodeAndHotel_Id(locationCode,hotelId);
        return roomEntities.stream().map(roomMapper::roomEntityToRoomResponse).toList();
    }

    // Hàm chuẩn hóa chuỗi (loại bỏ dấu tiếng Việt)
    private String removeVietnameseAccents(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D").toLowerCase();
    }

}