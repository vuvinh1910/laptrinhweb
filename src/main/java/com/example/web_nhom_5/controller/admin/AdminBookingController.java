package com.example.web_nhom_5.controller.admin;


import com.example.web_nhom_5.dto.api.ApiResponse;
import com.example.web_nhom_5.dto.response.BookingRoomResponse;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.service.BookingRoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/bookings")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminBookingController {
    BookingRoomService bookingRoomService;


    // lay tat ca cac don dat phong
    @GetMapping(value = {"/rooms","/rooms/"})
    public ApiResponse<List<BookingRoomResponse>> getAllBookingRooms() {
        return ApiResponse.<List<BookingRoomResponse>>builder()
                .result(bookingRoomService.getAllBookingRooms())
                .build();
    }

    // lay tat ca cac don dat phong theo trang thai dat phong(Pending,Confirmed,Cancelled)
    @GetMapping(value = "/rooms/by-status")
    public ApiResponse<List<BookingRoomResponse>> getBookingRoomByStatus(@RequestParam BookingStatus status) {
        return ApiResponse.<List<BookingRoomResponse>>builder()
                .result(bookingRoomService.getAllBookingRoomsByStatus(status))
                .build();
    }

    // cap nhat trang thai dat phong
    // co the dung ham nay de huy don,xac nhan don,...
    @PutMapping(value = "/rooms/{bookingRoomId}/status")
    public ApiResponse<BookingStatus> updateBookingStatusByBookingId(@PathVariable long bookingRoomId, @RequestParam BookingStatus status) {
        return ApiResponse.<BookingStatus>builder()
                .result(bookingRoomService.updateBookingStatusByBookingRoomId(bookingRoomId,status))
                .message("Status updated successfully")
                .build();
    }

    //lay tat ca cac don dat phong theo trang thai thanh toan
    @GetMapping(value = "/rooms/by-paid")
    public ApiResponse<List<BookingRoomResponse>> getBookingRoomByPaid(@RequestParam boolean paid) {
        return ApiResponse.<List<BookingRoomResponse>>builder()
                .result(bookingRoomService.getAllBookingRoomsByPaid(paid))
                .build();
    }
}
