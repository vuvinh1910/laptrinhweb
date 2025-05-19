package com.example.web_nhom_5.dto.response;

import lombok.Data;

@Data
public class HotelResponse {
    long id;
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
