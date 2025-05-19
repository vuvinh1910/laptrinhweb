package com.example.web_nhom_5.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateRequest {

    private String roomName;

    private long roomPrice;

    private String roomDetail;

    private String roomType;

    private long hotelId;
}
