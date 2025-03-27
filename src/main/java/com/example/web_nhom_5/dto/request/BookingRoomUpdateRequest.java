package com.example.web_nhom_5.dto.request;

import com.example.web_nhom_5.enums.BookingStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class BookingRoomUpdateRequest {
    private LocalDate checkIn;

    private LocalDate checkOut;

    private int numOfPeople;

    private long totalPrice;

    private boolean paid;

    private BookingStatus status;
}
