package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.conventer.ServiceMapper;
import com.example.web_nhom_5.dto.request.ServiceCreateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.entity.ServiceEntity;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.ServiceRepository;
import com.example.web_nhom_5.search.EntitySpecificationBuilder.ServiceSpecificationBuilder;
import com.example.web_nhom_5.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceMapper serviceMapper;

    @Override
    public ServiceEntity getServiceById(String id) {
        return serviceRepository.findById(id).orElseThrow(() -> new WebException(ErrorCode.SERVICE_NOT_FOUND));
    }

    @Override
    public ServiceResponse addService(ServiceCreateRequest serviceCreateRequest) {
        if (serviceRepository.existsById(serviceCreateRequest.getCodeName())) {
            throw new WebException(ErrorCode.SERVICE_EXISTED);
        }
        ServiceEntity serviceEntity = serviceMapper.serviceCreateRequestToServiceEntity(serviceCreateRequest);
        return serviceMapper.serviceEntityToServiceResponse(serviceRepository.save(serviceEntity));
    }

    @Override
    public ServiceResponse updateService(ServiceUpdateRequest serviceUpdateRequest, String id) {
        ServiceEntity serviceEntity = getServiceById(id);
        serviceMapper.updateService(serviceEntity, serviceUpdateRequest);
        return serviceMapper.serviceEntityToServiceResponse(serviceRepository.save(serviceEntity));
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream().map(serviceMapper::serviceEntityToServiceResponse).toList();
    }

    @Override
    public void deleteService(String serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    @Override
    public List<ServiceResponse> filterService(Long minPrice, Long maxPrice, String serviceType) {
        if(serviceType.isEmpty()) {
            serviceType = null;
        }
        List<ServiceEntity> serviceEntities = serviceRepository.filterService(minPrice, maxPrice,serviceType);
        return serviceEntities.stream().map(serviceMapper::serviceEntityToServiceResponse).toList();
    }

    @Override
    public List<ServiceResponse> listAllServiceByKeyword(String keyword)
    {
        List<ServiceEntity> serviceEntities=serviceRepository.listAllBykeyWord(keyword);
        return serviceEntities.stream().map(serviceMapper::serviceEntityToServiceResponse).toList();
    }
    @Override
    public List<ServiceResponse> filterBySpecification(String search)
    {
        ServiceSpecificationBuilder builder = new ServiceSpecificationBuilder();
        Pattern pattern = Pattern.compile("((\\w+?)([:<>!~])(\\w[\\w\\s]*))(\\p{Punct})?",Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(search+",");
        while (matcher.find()) {

            builder.with(
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    matcher.group(1),
                    matcher.group(5));
        }
        Specification<ServiceEntity> spec = builder.build();
        return serviceRepository.findAll(spec).stream().map(serviceMapper::serviceEntityToServiceResponse).toList();
    }
}
