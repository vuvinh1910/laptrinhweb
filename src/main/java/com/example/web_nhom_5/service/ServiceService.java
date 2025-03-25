package com.example.web_nhom_5.service;

import com.example.web_nhom_5.dto.request.ServiceCreateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.entity.ServiceEntity;

import java.util.List;

public interface ServiceService {
    ServiceEntity getServiceById(String id);
    ServiceResponse addService(ServiceCreateRequest serviceCreateRequest);
    ServiceResponse updateService(ServiceUpdateRequest serviceUpdateRequest, String id);
    List<ServiceResponse> getAllServices();
    void deleteService(String serviceId);

    // can them cac ham chuc nang tim kiem, loc theo yeu cau.
    List<ServiceResponse> listAllServiceByKeyword(String keyword);
    List<ServiceResponse> filterBySpecification(String search);
}
