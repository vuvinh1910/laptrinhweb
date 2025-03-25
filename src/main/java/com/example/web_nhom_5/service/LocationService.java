package com.example.web_nhom_5.service;

import com.example.web_nhom_5.dto.request.LocationUpdateRequest;
import com.example.web_nhom_5.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {
    List<LocationResponse> getAllLocation();
    LocationResponse getLocationById(String locationId);
    LocationResponse updateLocation(LocationUpdateRequest location, String locationId);
    void deleteLocation(String locationId);
}
