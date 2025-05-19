package com.example.web_nhom_5.dto.request;

import lombok.Data;

@Data
public class HotelCreateRequest {
    String address;
    String nameHotel;
    String description;
    String countTotalBook;
    String intro;
    String phone;
    long price;
    int star;
    double rate;
    int voucher;
    String locationCode;
}
