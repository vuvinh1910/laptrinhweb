package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.BookingRoomCreateRequest;
import com.example.web_nhom_5.dto.request.BookingRoomUpdateRequest;
import com.example.web_nhom_5.dto.response.BookingRoomResponse;
import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class BookingRoomMapperImpl implements BookingRoomMapper {

    @Override
    public BookingRoomEntity bookingRoomCreateRequestToBookingRoomEntity(BookingRoomCreateRequest bookingRoomCreateRequest) {
        if ( bookingRoomCreateRequest == null ) {
            return null;
        }

        BookingRoomEntity bookingRoomEntity = new BookingRoomEntity();

        bookingRoomEntity.setCheckIn( bookingRoomCreateRequest.getCheckIn() );
        bookingRoomEntity.setCheckOut( bookingRoomCreateRequest.getCheckOut() );
        bookingRoomEntity.setNumOfPeople( bookingRoomCreateRequest.getNumOfPeople() );

        return bookingRoomEntity;
    }

    @Override
    public BookingRoomResponse bookingRoomEntityToBookingRoomResponse(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return null;
        }

        BookingRoomResponse bookingRoomResponse = new BookingRoomResponse();

        bookingRoomResponse.setFullName( bookingRoomEntityUserFullName( bookingRoomEntity ) );
        bookingRoomResponse.setRoomName( bookingRoomEntityRoomRoomName( bookingRoomEntity ) );
        bookingRoomResponse.setRoomId( bookingRoomEntityRoomId( bookingRoomEntity ) );
        bookingRoomResponse.setUserId( bookingRoomEntityUserId( bookingRoomEntity ) );
        bookingRoomResponse.setId( bookingRoomEntity.getId() );
        bookingRoomResponse.setStatus( bookingRoomEntity.getStatus() );
        bookingRoomResponse.setPaid( bookingRoomEntity.isPaid() );
        bookingRoomResponse.setCheckIn( bookingRoomEntity.getCheckIn() );
        bookingRoomResponse.setCheckOut( bookingRoomEntity.getCheckOut() );
        bookingRoomResponse.setNumOfPeople( bookingRoomEntity.getNumOfPeople() );
        bookingRoomResponse.setTotalPrice( bookingRoomEntity.getTotalPrice() );

        return bookingRoomResponse;
    }

    @Override
    public void updateBookingRoom(BookingRoomEntity bookingRoomEntity, BookingRoomUpdateRequest bookingRoomUpdateRequest) {
        if ( bookingRoomUpdateRequest == null ) {
            return;
        }

        if ( bookingRoomUpdateRequest.getStatus() != null ) {
            bookingRoomEntity.setStatus( bookingRoomUpdateRequest.getStatus() );
        }
        if ( bookingRoomUpdateRequest.getCheckIn() != null ) {
            bookingRoomEntity.setCheckIn( bookingRoomUpdateRequest.getCheckIn() );
        }
        if ( bookingRoomUpdateRequest.getCheckOut() != null ) {
            bookingRoomEntity.setCheckOut( bookingRoomUpdateRequest.getCheckOut() );
        }
        bookingRoomEntity.setNumOfPeople( bookingRoomUpdateRequest.getNumOfPeople() );
        bookingRoomEntity.setTotalPrice( bookingRoomUpdateRequest.getTotalPrice() );
        bookingRoomEntity.setPaid( bookingRoomUpdateRequest.isPaid() );
    }

    private String bookingRoomEntityUserFullName(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return null;
        }
        UserEntity user = bookingRoomEntity.getUser();
        if ( user == null ) {
            return null;
        }
        String fullName = user.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private String bookingRoomEntityRoomRoomName(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return null;
        }
        RoomEntity room = bookingRoomEntity.getRoom();
        if ( room == null ) {
            return null;
        }
        String roomName = room.getRoomName();
        if ( roomName == null ) {
            return null;
        }
        return roomName;
    }

    private long bookingRoomEntityRoomId(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return 0L;
        }
        RoomEntity room = bookingRoomEntity.getRoom();
        if ( room == null ) {
            return 0L;
        }
        long id = room.getId();
        return id;
    }

    private long bookingRoomEntityUserId(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return 0L;
        }
        UserEntity user = bookingRoomEntity.getUser();
        if ( user == null ) {
            return 0L;
        }
        long id = user.getId();
        return id;
    }
}
