package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.BookingServiceCreateRequest;
import com.example.web_nhom_5.dto.request.BookingServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.BookingServiceResponse;
import com.example.web_nhom_5.entity.BookingServiceEntity;
import com.example.web_nhom_5.entity.ServiceEntity;
import com.example.web_nhom_5.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class BookingServiceMapperImpl implements BookingServiceMapper {

    @Override
    public BookingServiceEntity bookingServiceCreateToBookingServiceEntity(BookingServiceCreateRequest bookingServiceCreateRequest) {
        if ( bookingServiceCreateRequest == null ) {
            return null;
        }

        BookingServiceEntity bookingServiceEntity = new BookingServiceEntity();

        return bookingServiceEntity;
    }

    @Override
    public BookingServiceResponse bookingServiceEntityToBookingServiceResponse(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }

        BookingServiceResponse bookingServiceResponse = new BookingServiceResponse();

        bookingServiceResponse.setFullName( bookingServiceEntityUserFullName( bookingServiceEntity ) );
        bookingServiceResponse.setServiceName( bookingServiceEntityServiceServiceName( bookingServiceEntity ) );
        bookingServiceResponse.setServiceId( bookingServiceEntityServiceCodeName( bookingServiceEntity ) );
        bookingServiceResponse.setId( bookingServiceEntity.getId() );
        bookingServiceResponse.setTotalPrice( bookingServiceEntity.getTotalPrice() );
        bookingServiceResponse.setPaid( bookingServiceEntity.isPaid() );
        if ( bookingServiceEntity.getStatus() != null ) {
            bookingServiceResponse.setStatus( bookingServiceEntity.getStatus().name() );
        }

        return bookingServiceResponse;
    }

    @Override
    public void updateBookingService(BookingServiceEntity bookingServiceEntity, BookingServiceUpdateRequest bookingServiceUpdateRequest) {
        if ( bookingServiceUpdateRequest == null ) {
            return;
        }

        bookingServiceEntity.setTotalPrice( bookingServiceUpdateRequest.getTotalPrice() );
        bookingServiceEntity.setPaid( bookingServiceUpdateRequest.isPaid() );
        if ( bookingServiceUpdateRequest.getStatus() != null ) {
            bookingServiceEntity.setStatus( bookingServiceUpdateRequest.getStatus() );
        }
    }

    private String bookingServiceEntityUserFullName(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }
        UserEntity user = bookingServiceEntity.getUser();
        if ( user == null ) {
            return null;
        }
        String fullName = user.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private String bookingServiceEntityServiceServiceName(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }
        ServiceEntity service = bookingServiceEntity.getService();
        if ( service == null ) {
            return null;
        }
        String serviceName = service.getServiceName();
        if ( serviceName == null ) {
            return null;
        }
        return serviceName;
    }

    private String bookingServiceEntityServiceCodeName(BookingServiceEntity bookingServiceEntity) {
        if ( bookingServiceEntity == null ) {
            return null;
        }
        ServiceEntity service = bookingServiceEntity.getService();
        if ( service == null ) {
            return null;
        }
        String codeName = service.getCodeName();
        if ( codeName == null ) {
            return null;
        }
        return codeName;
    }
}
