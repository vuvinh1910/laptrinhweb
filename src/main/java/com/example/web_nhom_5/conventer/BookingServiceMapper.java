package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.BookingServiceCreateRequest;
import com.example.web_nhom_5.dto.response.BookingServiceResponse;
import com.example.web_nhom_5.entity.BookingServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingServiceMapper {
    // mapstruct co chuyen enum sang string va nguoc lai ?.

    @Mapping(target = "status", ignore = true)
    BookingServiceEntity bookingServiceCreateToBookingServiceEntity(BookingServiceCreateRequest bookingServiceCreateRequest);

    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "service.serviceName", target = "serviceName")
    @Mapping(source = "service.codeName", target = "serviceId")
    BookingServiceResponse bookingServiceEntityToBookingServiceResponse(BookingServiceEntity bookingServiceEntity);
}
