package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.conventer.BookingRoomMapper;
import com.example.web_nhom_5.conventer.PaymentMapper;
import com.example.web_nhom_5.dto.EmailBody;
import com.example.web_nhom_5.dto.request.BookingRoomCreateRequest;
import com.example.web_nhom_5.dto.request.BookingRoomUpdateRequest;
import com.example.web_nhom_5.dto.response.BookingRoomResponse;
import com.example.web_nhom_5.dto.response.ProcessPaymentResponse;
import com.example.web_nhom_5.entity.*;
import com.example.web_nhom_5.enums.BookingStatus;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.*;
import com.example.web_nhom_5.service.BookingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BookingRoomServiceImpl implements BookingRoomService {
    @Autowired
    private BookingRoomRepository bookingRoomRepository;

    @Autowired
    private BookingRoomMapper bookingRoomMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private UserVoucherService userVoucherService;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private UserVoucherRepository userVoucherRepository;
    @Autowired
    private UserService userService;

    @Override
    public BookingRoomResponse addBookingRoom(BookingRoomCreateRequest bookingRoomCreateRequest) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUserName(name).orElseThrow(()
                -> new WebException(ErrorCode.USER_NOT_EXISTED) );

        BookingRoomEntity bookingRoomEntity = bookingRoomMapper.bookingRoomCreateRequestToBookingRoomEntity(bookingRoomCreateRequest);
        RoomEntity roomEntity = roomRepository.findById(bookingRoomCreateRequest.getRoomId()).orElseThrow(() -> new WebException(ErrorCode.ROOM_NOT_FOUND));

        if (!bookingRoomIsAvailable(bookingRoomCreateRequest.getRoomId(), bookingRoomCreateRequest.getCheckIn(), bookingRoomCreateRequest.getCheckOut())) {
            throw new WebException(ErrorCode.ROOM_FULL);
        }

        bookingRoomEntity.setRoom(roomEntity);
        bookingRoomEntity.setUser(userEntity);

        userEntity.getBookingRooms().add(bookingRoomEntity);
        roomEntity.getBookingRooms().add(bookingRoomEntity);
        long dayBetween = ChronoUnit.DAYS.between(bookingRoomCreateRequest.getCheckIn(), bookingRoomCreateRequest.getCheckOut());
        bookingRoomEntity.setTotalPrice(roomEntity.getRoomPrice()*(dayBetween));
        BookingRoomEntity saveBookingRoomEntity = bookingRoomRepository.save(bookingRoomEntity);
        return bookingRoomMapper.bookingRoomEntityToBookingRoomResponse(saveBookingRoomEntity);
    }

    @Override
    public BookingRoomEntity getBookingRoomById(Long bookingRoomId) {
        return bookingRoomRepository.findById(bookingRoomId).orElseThrow(() -> new WebException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public void updateRefundBookingById(Long bookingRoomId) {
        BookingRoomEntity bookingRoomEntity = getBookingRoomById(bookingRoomId);
        bookingRoomEntity.setPaid(false);
        bookingRoomEntity.setStatus(BookingStatus.COMPLETED);
        RefundEntity refund = bookingRoomEntity.getRefund();
        refund.setCompleted(true);
        bookingRoomRepository.save(bookingRoomEntity);
        refundRepository.save(refund);
        EmailBody email = EmailBody.builder()
                .to(bookingRoomEntity.getUser().getEmail())
                .subject("Thông báo hoàn tiền")
                .text("Yêu cầu hoàn tiền của bạn đã được chấp nhận." +
                        "\nSố tiền :"+ bookingRoomEntity.getUserPaid() + " VND" +
                        "\nVui long kiểm tra số dư tài khoản.")
                .build();
        emailService.sendEmail(email);
    }

    @Override
    public BookingStatus updateBookingStatusByBookingRoomId(Long bookingRoomId, BookingStatus bookingStatus) {
        BookingRoomEntity bookingRoomEntity = getBookingRoomById(bookingRoomId);
        if(bookingStatus.equals(BookingStatus.CONFIRMED) && !bookingRoomEntity.isPaid()) {
            throw new WebException(ErrorCode.BOOKING_IS_NOT_PAID);
        }
        if(bookingRoomEntity.getStatus().equals(BookingStatus.COMPLETED) || bookingRoomEntity.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new WebException(ErrorCode.DO_NOT_CHANGE_THIS);
        }
        bookingRoomEntity.setStatus(bookingStatus);
        bookingRoomRepository.save(bookingRoomEntity);
        return bookingRoomEntity.getStatus();
    }

    @Override
    public void updateBookingRoom(Long bookingRoomId, BookingRoomUpdateRequest bookingRoomUpdateRequest) {
        BookingRoomEntity bookingRoomEntity = getBookingRoomById(bookingRoomId);
        bookingRoomMapper.updateBookingRoom(bookingRoomEntity,bookingRoomUpdateRequest);
        bookingRoomRepository.save(bookingRoomEntity);
    }

    @Override
    public void deleteBookingRoomById(Long bookingRoomId) {
        bookingRoomRepository.deleteById(bookingRoomId);
    }

    @Override
    public List<BookingRoomResponse> getAllBookingRooms() {
        return bookingRoomRepository.findAll().stream().map(bookingRoomMapper::bookingRoomEntityToBookingRoomResponse).toList();
    }

    @Override
    public List<BookingRoomResponse> getAllBookingRoomsByStatus(BookingStatus bookingStatus) {
        return bookingRoomRepository.findAllByStatus(bookingStatus)
                .stream()
                .map(bookingRoomMapper::bookingRoomEntityToBookingRoomResponse)
                .toList();
    }

    @Override
    public List<BookingRoomResponse> getAllBookingRoomsByUserAndStatusAndPaid(BookingStatus status, Boolean paid) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByUserName(userName).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        return bookingRoomRepository.findAllByUser_IdAndStatusAndPaid(userEntity.getId(),status,paid)
                .stream()
                .map(bookingRoomMapper::bookingRoomEntityToBookingRoomResponse)
                .toList();
    }

    @Override
    public List<BookingRoomEntity> getAllBookingRoomsByRoomId(Long roomId) {
        return bookingRoomRepository.findAllByRoom_Id(roomId);
    }


    @Override
    public boolean bookingRoomIsAvailable(Long roomId,LocalDate checkIn, LocalDate checkOut) {
        if (checkIn!=null && checkOut!=null && checkIn.isAfter(checkOut)) {
            throw new WebException(ErrorCode.INVALID_BOOKING_CHECKIN_CHECKOUT);
        }
        long count = bookingRoomRepository.countRoomAvailable(roomId,checkIn,checkOut,BookingStatus.CONFIRMED,true);
        return count == 0;
    }

    @Override
    public List<BookingRoomResponse> getAllBookingRoomsByPaid(boolean paid) {
        return bookingRoomRepository.findAllByPaid(paid)
                .stream()
                .filter(bookingRoomEntity -> !bookingRoomEntity.getStatus().equals(BookingStatus.CANCELLED))
                .map(bookingRoomMapper::bookingRoomEntityToBookingRoomResponse)
                .toList();
    }

    @Override
    public long countByStatus(BookingStatus status) {
        return bookingRoomRepository.countAllByStatus(status);
    }

    @Override
    public long countPendingComplete() {
        LocalDate now = LocalDate.now();
        return bookingRoomRepository.countPendingCheckOut(now,BookingStatus.CONFIRMED);
    }

    @Override
    public long sumTotalPrice() {
        return bookingRoomRepository.sumAllPriceByStatus(true);
    }

//    @Override
//    @Scheduled(fixedRate = 120000)
//    public void cancelExpiredBooking() {
//        LocalDateTime oneHourAgo = LocalDateTime.now().minusMinutes(10);
//        List<BookingRoomEntity> expiredBookings = bookingRoomRepository.findAllByStatusAndCreatedAtBeforeAndPaid(BookingStatus.PENDING, oneHourAgo, false);
//        expiredBookings.forEach(booking -> booking.setStatus(BookingStatus.CANCELLED));
//        bookingRoomRepository.saveAll(expiredBookings);
//        System.out.println("Canceled " + expiredBookings.size() + " expired bookings room");
//    }

    @Override
    public ProcessPaymentResponse processPayment(long bookingId, String voucherCode) {
        BookingRoomEntity bookingRoomEntity = getBookingRoomById(bookingId);
        Long id = Long.parseLong(userService.getMyInfo().getId());
        int sale = 0;
        if(!voucherCode.isEmpty() && voucherCode!=null) {
            UserVoucherEntity voucher = userVoucherRepository.findByVoucher_CodeAndUser_Id(voucherCode,id);
            voucher.setUsed(true);
            sale = voucher.getVoucher().getValue();
            userVoucherRepository.save(voucher);
        }
        if (bookingRoomEntity.isPaid() || bookingRoomEntity.getStatus().equals(BookingStatus.COMPLETED) || bookingRoomEntity.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new WebException(ErrorCode.BOOKING_IS_PAID);
        }
        if (bookingRoomEntity.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new WebException(ErrorCode.BOOKING_HAS_BEEN_CANCELED);
        }

        if(!bookingRoomIsAvailable(bookingRoomEntity.getRoom().getId(),bookingRoomEntity.getCheckIn(),bookingRoomEntity.getCheckOut())) {
            throw new WebException(ErrorCode.ROOM_IS_OUT);
        }
        bookingRoomEntity.setPaid(true);
        bookingRoomEntity.setStatus(BookingStatus.CONFIRMED);
        long totalPrice = bookingRoomEntity.getTotalPrice();
        long userPaid = totalPrice - totalPrice*sale/100;
        bookingRoomEntity.setUserPaid(userPaid);
        bookingRoomRepository.save(bookingRoomEntity);
        ProcessPaymentResponse processPaymentResponse = paymentMapper.bookingRoomToPaymentResponse(bookingRoomEntity);
        processPaymentResponse.setAmount(bookingRoomEntity.getTotalPrice());
        processPaymentResponse.setCashBack(0);
        processPaymentResponse.setSuccess(true);
        return processPaymentResponse;
    }

    @Override
    public List<BookingRoomEntity> filterBookingRooms(BookingStatus status, Boolean isPaid) {
        List<BookingRoomEntity> filterBookingRooms;

        if (status != null && isPaid != null) {
            filterBookingRooms = bookingRoomRepository.findAllByStatusAndPaid(status, isPaid);
        } else if (status != null ) {
            filterBookingRooms = bookingRoomRepository.findAllByStatus(status);
        } else if (isPaid != null) {
            filterBookingRooms = bookingRoomRepository.findAllByPaid(isPaid);
        } else {
            filterBookingRooms = bookingRoomRepository.findAll();
        }

        return filterBookingRooms;
    }

}
