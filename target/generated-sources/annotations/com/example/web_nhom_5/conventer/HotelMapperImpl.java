package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.HotelCreateRequest;
import com.example.web_nhom_5.dto.request.HotelUpdateRequest;
import com.example.web_nhom_5.dto.response.HotelResponse;
import com.example.web_nhom_5.entity.HotelEntity;
import com.example.web_nhom_5.entity.LocationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class HotelMapperImpl implements HotelMapper {

    @Override
    public void updateEntity(HotelEntity hotelEntity, HotelUpdateRequest hotelUpdateRequest) {
        if ( hotelUpdateRequest == null ) {
            return;
        }

        if ( hotelUpdateRequest.getNameHotel() != null ) {
            hotelEntity.setNameHotel( hotelUpdateRequest.getNameHotel() );
        }
        if ( hotelUpdateRequest.getAddress() != null ) {
            hotelEntity.setAddress( hotelUpdateRequest.getAddress() );
        }
        if ( hotelUpdateRequest.getPhone() != null ) {
            hotelEntity.setPhone( hotelUpdateRequest.getPhone() );
        }
        hotelEntity.setStar( hotelUpdateRequest.getStar() );
        hotelEntity.setRate( hotelUpdateRequest.getRate() );
        hotelEntity.setCountTotalBook( hotelUpdateRequest.getCountTotalBook() );
        if ( hotelUpdateRequest.getIntro() != null ) {
            hotelEntity.setIntro( hotelUpdateRequest.getIntro() );
        }
        if ( hotelUpdateRequest.getDescription() != null ) {
            hotelEntity.setDescription( hotelUpdateRequest.getDescription() );
        }
        hotelEntity.setVoucher( hotelUpdateRequest.getVoucher() );
        hotelEntity.setPrice( hotelUpdateRequest.getPrice() );
    }

    @Override
    public HotelResponse hotelToResponse(HotelEntity hotelEntity) {
        if ( hotelEntity == null ) {
            return null;
        }

        HotelResponse hotelResponse = new HotelResponse();

        hotelResponse.setLocationCode( hotelEntityLocationLocationCode( hotelEntity ) );
        hotelResponse.setId( hotelEntity.getId() );
        hotelResponse.setAddress( hotelEntity.getAddress() );
        hotelResponse.setNameHotel( hotelEntity.getNameHotel() );
        hotelResponse.setDescription( hotelEntity.getDescription() );
        hotelResponse.setCountTotalBook( String.valueOf( hotelEntity.getCountTotalBook() ) );
        hotelResponse.setIntro( hotelEntity.getIntro() );
        hotelResponse.setPhone( hotelEntity.getPhone() );
        if ( hotelEntity.getPrice() != null ) {
            hotelResponse.setPrice( hotelEntity.getPrice() );
        }
        hotelResponse.setStar( hotelEntity.getStar() );
        hotelResponse.setRate( hotelEntity.getRate() );
        hotelResponse.setVoucher( hotelEntity.getVoucher() );

        return hotelResponse;
    }

    @Override
    public void hotelCreateToEntity(HotelEntity hotelEntity, HotelCreateRequest hotelResponse) {
        if ( hotelResponse == null ) {
            return;
        }

        if ( hotelResponse.getNameHotel() != null ) {
            hotelEntity.setNameHotel( hotelResponse.getNameHotel() );
        }
        if ( hotelResponse.getAddress() != null ) {
            hotelEntity.setAddress( hotelResponse.getAddress() );
        }
        if ( hotelResponse.getPhone() != null ) {
            hotelEntity.setPhone( hotelResponse.getPhone() );
        }
        hotelEntity.setStar( hotelResponse.getStar() );
        hotelEntity.setRate( hotelResponse.getRate() );
        if ( hotelResponse.getCountTotalBook() != null ) {
            hotelEntity.setCountTotalBook( Integer.parseInt( hotelResponse.getCountTotalBook() ) );
        }
        if ( hotelResponse.getIntro() != null ) {
            hotelEntity.setIntro( hotelResponse.getIntro() );
        }
        if ( hotelResponse.getDescription() != null ) {
            hotelEntity.setDescription( hotelResponse.getDescription() );
        }
        hotelEntity.setVoucher( hotelResponse.getVoucher() );
        hotelEntity.setPrice( hotelResponse.getPrice() );
    }

    private String hotelEntityLocationLocationCode(HotelEntity hotelEntity) {
        if ( hotelEntity == null ) {
            return null;
        }
        LocationEntity location = hotelEntity.getLocation();
        if ( location == null ) {
            return null;
        }
        String locationCode = location.getLocationCode();
        if ( locationCode == null ) {
            return null;
        }
        return locationCode;
    }
}
