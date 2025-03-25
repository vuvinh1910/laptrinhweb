package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.response.ProcessPaymentResponse;
import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.entity.BookingServiceEntity;
import com.example.web_nhom_5.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public ProcessPaymentResponse bookingRoomToPaymentResponse(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return null;
        }

        ProcessPaymentResponse processPaymentResponse = new ProcessPaymentResponse();

        processPaymentResponse.setBookingId( bookingRoomEntity.getId() );
        processPaymentResponse.setUserName( bookingRoomEntityUserUserName( bookingRoomEntity ) );

        return processPaymentResponse;
    }

    @Override
    public ProcessPaymentResponse bookingServiceToPaymentResponse(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }

        ProcessPaymentResponse processPaymentResponse = new ProcessPaymentResponse();

        processPaymentResponse.setBookingId( bookingServiceEntity.getId() );
        processPaymentResponse.setUserName( bookingServiceEntityUserUserName( bookingServiceEntity ) );

        return processPaymentResponse;
    }

    private String bookingRoomEntityUserUserName(BookingRoomEntity bookingRoomEntity) {
        if ( bookingRoomEntity == null ) {
            return null;
        }
        UserEntity user = bookingRoomEntity.getUser();
        if ( user == null ) {
            return null;
        }
        String userName = user.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }

    private String bookingServiceEntityUserUserName(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }
        UserEntity user = bookingServiceEntity.getUser();
        if ( user == null ) {
            return null;
        }
        String userName = user.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }
}
