package com.example.web_nhom_5.service;

import com.example.web_nhom_5.dto.request.BookingRoomCreateRequest;
import com.example.web_nhom_5.dto.request.BookingRoomUpdateRequest;
import com.example.web_nhom_5.dto.response.BookingRoomResponse;
import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingRoomService extends ProcessPaymentService{
    BookingRoomResponse addBookingRoom(BookingRoomCreateRequest bookingRoomCreateRequest);
    BookingRoomEntity getBookingRoomById(Long bookingRoomId);
    void updateRefundBookingById(Long bookingRoomId);
    BookingStatus updateBookingStatusByBookingRoomId(Long bookingRoomId, BookingStatus bookingStatus);
    void updateBookingRoom(Long bookingRoomId, BookingRoomUpdateRequest bookingRoomUpdateRequest);

    void deleteBookingRoomById(Long bookingRoomId);
    List<BookingRoomResponse> getAllBookingRooms();
    List<BookingRoomResponse> getAllBookingRoomsByStatus(BookingStatus bookingStatus);
    List<BookingRoomResponse> getAllBookingRoomsByUserAndStatusAndPaid(BookingStatus status, Boolean paid);
    List<BookingRoomEntity> getAllBookingRoomsByRoomId(Long roomId);
    boolean bookingRoomIsAvailable(Long roomId,LocalDate checkIn,LocalDate checkOut);
    List<BookingRoomResponse> getAllBookingRoomsByPaid(boolean paid);
    long countByStatus(BookingStatus status);
    long countPendingComplete();
    long sumTotalPrice();
    List<BookingRoomEntity> filterBookingRooms(BookingStatus status, Boolean isPaid);
}
