package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoomEntity, Long> {
    List<BookingRoomEntity> findAllByUser_Id(Long userId);
    List<BookingRoomEntity> findAllByStatus(BookingStatus status);
    @Query("SELECT COUNT(b) FROM BookingRoomEntity b WHERE b.checkOut > :now and b.status = :status")
    long countPendingCheckOut(LocalDate now, BookingStatus status);
    @Query("SELECT count(b) from BookingRoomEntity b where b.status = :status and b.paid = :paid")
    long countPendingConfirm(BookingStatus status, Boolean paid);
    @Query("select coalesce(sum(b.totalPrice),0) from BookingRoomEntity b where b.paid = :paid")
    long sumAllPriceByStatus(boolean paid);
    List<BookingRoomEntity> findAllByRoom_Id(Long roomId);
    List<BookingRoomEntity> findAllByPaid(boolean paid);
    List<BookingRoomEntity> findAllByStatusAndPaid(BookingStatus status, boolean paid);
    List<BookingRoomEntity> findAllByStatusAndCreatedAtBeforeAndPaid(BookingStatus status, LocalDateTime createdAt, boolean paid);

    @Query("SELECT COUNT(b) FROM BookingRoomEntity b " +
            "WHERE b.room.id = :roomId " +
            "AND (:checkOut IS NULL OR b.checkOut > :checkIn) " +
            "AND (:checkIn IS NULL OR b.checkIn < :checkOut) " +
            "AND b.status = :status")
    long countRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, BookingStatus status);
}
