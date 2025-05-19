package com.example.web_nhom_5.service.implement;


import com.example.web_nhom_5.conventer.HotelMapper;
import com.example.web_nhom_5.dto.request.HotelCreateRequest;
import com.example.web_nhom_5.dto.request.HotelUpdateRequest;
import com.example.web_nhom_5.entity.HotelEntity;
import com.example.web_nhom_5.entity.LocationEntity;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.HotelRepository;
import com.example.web_nhom_5.repository.LocationRepository;
import com.example.web_nhom_5.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HotelMapper hotelMapper;
    @Autowired
    private LocationRepository locationRepository;

    @Override
    public List<HotelEntity> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public HotelEntity getHotelById(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new WebException(ErrorCode.HOTEL_NOT_FOUND));
    }

    @Override
    public List<HotelEntity> getAllHotelsByLocation(String location_id) {
        return hotelRepository.findAllByLocation_LocationCode(location_id);
    }

    @Override
    public List<HotelEntity> hotelFilter(String location_id, Integer star, Double rate, Long price) {
        String locationCode = null;
        if (!location_id.isEmpty()) {
            location_id = removeVietnameseAccents(location_id).toLowerCase();
            locationCode = switch (location_id) {
                case "hn", "hanoi", "ha noi", "ha-noi" -> "HA-NOI";
                case "hcm", "hochiminh", "ho chi minh", "ho-chi-minh" -> "HCM";
                case "dn", "danang", "da nang", "da-nang" -> "DA-NANG";
                case "ha", "hoian", "hoi an", "hoi-an" -> "HOI-AN";
                case "pq", "phuquoc", "phu quoc", "phu-quoc" -> "PHU-QUOC";
                default -> "~";
            };
        }
        return hotelRepository.hotelFilter(locationCode, star, rate, price);
    }

    // Hàm chuẩn hóa chuỗi (loại bỏ dấu tiếng Việt)
    private String removeVietnameseAccents(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(normalized).replaceAll("").replace("đ", "d").replace("Đ", "D").toLowerCase();
    }

    @Override
    public void updateMinPrice() {
        hotelRepository.updateAllHotelMinPrices();
    }


    @Override
    public void updateHotel(HotelUpdateRequest hotel, Long id) {
        HotelEntity hotelEntity = hotelRepository.findById(id).orElseThrow(()->new WebException(ErrorCode.HOTEL_NOT_FOUND));
        hotelMapper.updateEntity(hotelEntity,hotel);
        LocationEntity old = locationRepository.findById(hotelEntity.getLocation().getLocationCode()).orElseThrow(()->new WebException(ErrorCode.HOTEL_NOT_FOUND));
        old.getHotels().remove(hotelEntity);
        LocationEntity newLocation = locationRepository.findById(hotel.getLocationCode()).orElseThrow(()->new WebException(ErrorCode.HOTEL_NOT_FOUND));
        newLocation.getHotels().add(hotelEntity);
        hotelEntity.setLocation(newLocation);
        hotelRepository.save(hotelEntity);
    }

    @Override
    public void addHotel(HotelCreateRequest hotel) {
        HotelEntity hotelEntity = new HotelEntity();
        hotelMapper.hotelCreateToEntity(hotelEntity,hotel);
        LocationEntity location = locationRepository.findById(hotel.getLocationCode()).orElseThrow(()->new WebException(ErrorCode.HOTEL_NOT_FOUND));
        location.getHotels().add(hotelEntity);
        hotelEntity.setLocation(location);
        hotelEntity.setRooms(new ArrayList<>());
        hotelRepository.save(hotelEntity);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
