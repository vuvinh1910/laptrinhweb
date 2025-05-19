package com.example.web_nhom_5.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateRequest {
    @NotNull
    private String roomName;

    @NotNull
    private long roomPrice;

    @NotNull
    private String roomDetail;

    @NotNull
    private String roomType;

    @NotNull
    private Long hotelId;
}
