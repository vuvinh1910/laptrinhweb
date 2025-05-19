package com.example.web_nhom_5.service.implement;


import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.entity.RefundEntity;
import com.example.web_nhom_5.repository.BookingRoomRepository;
import com.example.web_nhom_5.repository.RefundRepository;
import com.example.web_nhom_5.service.BookingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RefundService {
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRoomService bookingRoomService;
    @Autowired
    private BookingRoomRepository bookingRoomRepository;

    public void createRefund(RefundEntity refundEntity, long bookingId) {
        BookingRoomEntity booking = bookingRoomService.getBookingRoomById(bookingId);
        refundEntity.setBooking(booking);
        booking.setRefund(refundEntity);
        refundRepository.save(refundEntity);
    }

    public List<RefundEntity> getAllRefund() {
        return refundRepository.findAll();
    }

    public void deleteRefund(long refundId) {
        refundRepository.deleteById(refundId);
    }
}
