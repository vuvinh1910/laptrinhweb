package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.ServiceCreateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceMapper {
    ServiceEntity serviceCreateRequestToServiceEntity(ServiceCreateRequest serviceCreateRequest);
    ServiceResponse serviceEntityToServiceResponse(ServiceEntity serviceEntity);
    @Mapping(target = "codeName", ignore = true)
    void updateService(@MappingTarget ServiceEntity serviceEntity, ServiceUpdateRequest serviceUpdateRequest);
}
