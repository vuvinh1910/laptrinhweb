package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.RoomCreateRequest;
import com.example.web_nhom_5.dto.request.RoomUpdateRequest;
import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.entity.HotelEntity;
import com.example.web_nhom_5.entity.LocationEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Override
    public RoomEntity roomCreateDtoToEntity(RoomCreateRequest roomCreateRequest) {
        if ( roomCreateRequest == null ) {
            return null;
        }

        RoomEntity roomEntity = new RoomEntity();

        roomEntity.setRoomName( roomCreateRequest.getRoomName() );
        roomEntity.setRoomPrice( roomCreateRequest.getRoomPrice() );
        roomEntity.setRoomDetail( roomCreateRequest.getRoomDetail() );
        roomEntity.setRoomType( roomCreateRequest.getRoomType() );

        return roomEntity;
    }

    @Override
    public RoomResponse roomEntityToRoomResponse(RoomEntity roomEntity) {
        if ( roomEntity == null ) {
            return null;
        }

        RoomResponse roomResponse = new RoomResponse();

        roomResponse.setLocationName( roomEntityHotelLocationLocationName( roomEntity ) );
        roomResponse.setHotelName( roomEntityHotelNameHotel( roomEntity ) );
        roomResponse.setHotelId( roomEntityHotelId( roomEntity ) );
        roomResponse.setId( roomEntity.getId() );
        roomResponse.setRoomName( roomEntity.getRoomName() );
        roomResponse.setRoomPrice( roomEntity.getRoomPrice() );
        roomResponse.setRoomDetail( roomEntity.getRoomDetail() );
        roomResponse.setRoomType( roomEntity.getRoomType() );

        return roomResponse;
    }

    @Override
    public void updateRoom(RoomEntity roomEntity, RoomUpdateRequest roomUpdateRequest) {
        if ( roomUpdateRequest == null ) {
            return;
        }

        if ( roomUpdateRequest.getRoomName() != null ) {
            roomEntity.setRoomName( roomUpdateRequest.getRoomName() );
        }
        roomEntity.setRoomPrice( roomUpdateRequest.getRoomPrice() );
        if ( roomUpdateRequest.getRoomDetail() != null ) {
            roomEntity.setRoomDetail( roomUpdateRequest.getRoomDetail() );
        }
        if ( roomUpdateRequest.getRoomType() != null ) {
            roomEntity.setRoomType( roomUpdateRequest.getRoomType() );
        }
    }

    private String roomEntityHotelLocationLocationName(RoomEntity roomEntity) {
        if ( roomEntity == null ) {
            return null;
        }
        HotelEntity hotel = roomEntity.getHotel();
        if ( hotel == null ) {
            return null;
        }
        LocationEntity location = hotel.getLocation();
        if ( location == null ) {
            return null;
        }
        String locationName = location.getLocationName();
        if ( locationName == null ) {
            return null;
        }
        return locationName;
    }

    private String roomEntityHotelNameHotel(RoomEntity roomEntity) {
        if ( roomEntity == null ) {
            return null;
        }
        HotelEntity hotel = roomEntity.getHotel();
        if ( hotel == null ) {
            return null;
        }
        String nameHotel = hotel.getNameHotel();
        if ( nameHotel == null ) {
            return null;
        }
        return nameHotel;
    }

    private long roomEntityHotelId(RoomEntity roomEntity) {
        if ( roomEntity == null ) {
            return 0L;
        }
        HotelEntity hotel = roomEntity.getHotel();
        if ( hotel == null ) {
            return 0L;
        }
        long id = hotel.getId();
        return id;
    }
}
