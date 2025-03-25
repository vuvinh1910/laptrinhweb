package com.example.web_nhom_5.dto.request;

import lombok.Data;

@Data
public class ServiceUpdateRequest {

    private String serviceName;

    private long servicePrice;

    private String serviceDetail;

}
