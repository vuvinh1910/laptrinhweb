package com.example.web_nhom_5.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class HotelUpdateRequest {
    private String nameHotel;

    private String address;

    private String phone;

    private String locationCode;

    private long price;

    private int star;

    private double rate;

    private int countTotalBook;

    private String intro;

    private String description;

    private int voucher;
}
