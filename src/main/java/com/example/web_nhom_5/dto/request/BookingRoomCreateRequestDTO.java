package com.example.web_nhom_5.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class BookingRoomCreateRequestDTO {

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate checkIn;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate checkOut;

    @NotNull
    @Min(value = 1)
    private int numOfPeople;

    private long roomId;

}
