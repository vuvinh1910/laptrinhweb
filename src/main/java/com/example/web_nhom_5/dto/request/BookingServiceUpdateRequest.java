package com.example.web_nhom_5.dto.request;

import com.example.web_nhom_5.enums.BookingStatus;
import lombok.Data;

@Data
public class BookingServiceUpdateRequest {
    private long totalPrice;
    private boolean isPaid;
    private BookingStatus status;
}
