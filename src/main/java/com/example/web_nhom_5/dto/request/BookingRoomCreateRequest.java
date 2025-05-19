package com.example.web_nhom_5.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class BookingRoomCreateRequest {

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate checkIn;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate checkOut;

    @NotNull
    @Min(value = 1)
    private int numOfPeople;

    @NotNull
    private String phone;

    private long roomId;

}
