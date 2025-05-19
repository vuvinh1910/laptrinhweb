package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.dto.response.RoomResponse;
import com.example.web_nhom_5.entity.RoomEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, JpaSpecificationExecutor<RoomEntity> {
    List<RoomEntity> findAllByRoomPriceLessThan(long roomPrice);
    List<RoomEntity> findAllByHotel_Id(Long id);
    @Query("SELECT r FROM RoomEntity r WHERE " +
            "(:locationCode IS NULL OR r.hotel.location.locationCode = :locationCode) AND " +
            "(:hotelId IS NULL OR r.hotel.id = :hotelId)")
    List<RoomEntity> findAllByHotel_Location_LocationCodeAndHotel_Id(String locationCode, Long hotelId);
    @Query("SELECT r FROM BookingRoomEntity r WHERE "  +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:paid IS NULL OR r.paid = :paid)")
    List<RoomEntity> findRoomsByFilter(
            @Param("status") String status,
            @Param("paid") String paid);
//    @Query("SELECT r FROM RoomEntity r WHERE Concat(r.hotelName,' ',r.roomName,' ',r.roomPrice,' ',r.roomDetail,' ',r.roomType,' ',r.location.locationCode,' ',r.location.locationName,' ') LIKE %?1%")
//    List<RoomEntity> listAll(String keyword);
//
//    @Query("select r from RoomEntity r where " +
//            "(:locationCode is null or r.location.locationCode = :locationCode) and " +
//            "(:price is null or r.roomPrice <= :price) and " +
//            "(:roomType is null or r.roomType like CONCAT(:roomType, '%'))"
//    )
//    List<RoomEntity> filterRooms(String locationCode, Long price, String roomType);
}
