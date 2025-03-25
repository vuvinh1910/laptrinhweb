package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.LocationUpdateRequest;
import com.example.web_nhom_5.dto.response.LocationResponse;
import com.example.web_nhom_5.entity.LocationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class LocationMapperImpl implements LocationMapper {

    @Override
    public LocationResponse locationEntityToLocationResponse(LocationEntity locationEntity) {
        if ( locationEntity == null ) {
            return null;
        }

        LocationResponse locationResponse = new LocationResponse();

        locationResponse.setLocationCode( locationEntity.getLocationCode() );
        locationResponse.setLocationName( locationEntity.getLocationName() );

        return locationResponse;
    }

    @Override
    public void updateLocation(LocationEntity locationEntity, LocationUpdateRequest locationUpdateRequest) {
        if ( locationUpdateRequest == null ) {
            return;
        }

        if ( locationUpdateRequest.getLocationCode() != null ) {
            locationEntity.setLocationCode( locationUpdateRequest.getLocationCode() );
        }
        if ( locationUpdateRequest.getLocationName() != null ) {
            locationEntity.setLocationName( locationUpdateRequest.getLocationName() );
        }
    }
}
