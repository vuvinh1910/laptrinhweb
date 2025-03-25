package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.ServiceCreateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.entity.ServiceEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class ServiceMapperImpl implements ServiceMapper {

    @Override
    public ServiceEntity serviceCreateRequestToServiceEntity(ServiceCreateRequest serviceCreateRequest) {
        if ( serviceCreateRequest == null ) {
            return null;
        }

        ServiceEntity serviceEntity = new ServiceEntity();

        serviceEntity.setCodeName( serviceCreateRequest.getCodeName() );
        serviceEntity.setServiceName( serviceCreateRequest.getServiceName() );
        serviceEntity.setServicePrice( serviceCreateRequest.getServicePrice() );
        serviceEntity.setServiceDetail( serviceCreateRequest.getServiceDetail() );

        return serviceEntity;
    }

    @Override
    public ServiceResponse serviceEntityToServiceResponse(ServiceEntity serviceEntity) {
        if ( serviceEntity == null ) {
            return null;
        }

        ServiceResponse serviceResponse = new ServiceResponse();

        serviceResponse.setCodeName( serviceEntity.getCodeName() );
        serviceResponse.setServiceName( serviceEntity.getServiceName() );
        serviceResponse.setServicePrice( serviceEntity.getServicePrice() );
        serviceResponse.setServiceDetail( serviceEntity.getServiceDetail() );

        return serviceResponse;
    }

    @Override
    public void updateService(ServiceEntity serviceEntity, ServiceUpdateRequest serviceUpdateRequest) {
        if ( serviceUpdateRequest == null ) {
            return;
        }

        if ( serviceUpdateRequest.getServiceName() != null ) {
            serviceEntity.setServiceName( serviceUpdateRequest.getServiceName() );
        }
        serviceEntity.setServicePrice( serviceUpdateRequest.getServicePrice() );
        if ( serviceUpdateRequest.getServiceDetail() != null ) {
            serviceEntity.setServiceDetail( serviceUpdateRequest.getServiceDetail() );
        }
    }
}
