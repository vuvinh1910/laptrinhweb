package com.example.web_nhom_5.service;

import com.example.web_nhom_5.dto.request.HotelCreateRequest;
import com.example.web_nhom_5.dto.request.HotelUpdateRequest;
import com.example.web_nhom_5.entity.HotelEntity;

import java.util.List;

public interface HotelService {
    public List<HotelEntity> getAllHotels();
    public HotelEntity getHotelById(Long id);
    public List<HotelEntity> getAllHotelsByLocation(String location_id);
    public List<HotelEntity> hotelFilter(String location_id, Integer star, Double rate, Long price);
    void updateHotel(HotelUpdateRequest hotel, Long id);
    void addHotel(HotelCreateRequest hotel);
    void deleteHotel(Long Id);
    void updateMinPrice();
}
