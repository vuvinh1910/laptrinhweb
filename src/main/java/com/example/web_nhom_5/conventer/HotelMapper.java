package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.HotelCreateRequest;
import com.example.web_nhom_5.dto.request.HotelUpdateRequest;
import com.example.web_nhom_5.dto.response.HotelResponse;
import com.example.web_nhom_5.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location",ignore = true)
    void updateEntity(@MappingTarget HotelEntity hotelEntity,HotelUpdateRequest hotelUpdateRequest);

    @Mapping(target = "locationCode", source = "location.locationCode")
    HotelResponse hotelToResponse(HotelEntity hotelEntity);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "location",ignore = true)
    @Mapping(target = "rooms",ignore = true)
    void hotelCreateToEntity(@MappingTarget HotelEntity hotelEntity,HotelCreateRequest hotelResponse);
}
