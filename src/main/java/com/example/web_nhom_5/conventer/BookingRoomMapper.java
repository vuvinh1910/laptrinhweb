package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.BookingRoomCreateRequest;
import com.example.web_nhom_5.dto.request.BookingRoomUpdateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.BookingRoomResponse;
import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingRoomMapper {

    @Mapping(target = "status", ignore = true)
    BookingRoomEntity bookingRoomCreateRequestToBookingRoomEntity(BookingRoomCreateRequest bookingRoomCreateRequest);

    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "user.id", target = "userId")
    BookingRoomResponse bookingRoomEntityToBookingRoomResponse(BookingRoomEntity bookingRoomEntity);

    @Mapping(target = "id", ignore = true)
    void updateBookingRoom(@MappingTarget BookingRoomEntity bookingRoomEntity, BookingRoomUpdateRequest bookingRoomUpdateRequest);
}
