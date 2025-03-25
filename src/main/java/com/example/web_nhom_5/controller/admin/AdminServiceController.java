package com.example.web_nhom_5.controller.admin;

import com.example.web_nhom_5.dto.api.ApiResponse;
import com.example.web_nhom_5.dto.request.ServiceCreateRequest;
import com.example.web_nhom_5.dto.request.ServiceUpdateRequest;
import com.example.web_nhom_5.dto.response.ServiceResponse;
import com.example.web_nhom_5.service.ServiceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/services")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminServiceController {
    ServiceService serviceService;

    @PostMapping
    public ApiResponse<ServiceResponse> createService(@Valid @RequestBody ServiceCreateRequest serviceCreateRequest) {
        return ApiResponse.<ServiceResponse>builder()
                .result(serviceService.addService(serviceCreateRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ServiceResponse>> getAllServices() {
        return ApiResponse.<List<ServiceResponse>>builder()
                .result(serviceService.getAllServices())
                .build();
    }

    @PutMapping(value = "/{serviceId}")
    public ApiResponse<ServiceResponse> updateService(@RequestBody ServiceUpdateRequest serviceUpdateRequest, @PathVariable("serviceId") String serviceId) {
        return ApiResponse.<ServiceResponse>builder()
                .result(serviceService.updateService(serviceUpdateRequest,serviceId))
                .build();
    }

    @DeleteMapping(value = "/{serviceId}")
    public ApiResponse<Object> deleteService(@PathVariable("serviceId") String serviceId) {
        serviceService.deleteService(serviceId);
        return ApiResponse.<Object>builder()
                .message("successfully")
                .build();
    }
}
