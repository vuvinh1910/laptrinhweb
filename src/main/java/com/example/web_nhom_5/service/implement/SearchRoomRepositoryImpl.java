//package com.example.web_nhom_5.service.implement;
//
//import com.example.web_nhom_5.entity.BookingRoomEntity;
//import com.example.web_nhom_5.entity.RoomEntity;
//import com.example.web_nhom_5.service.SearchRoomRepositoryCustom;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.criteria.*;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public class SearchRoomRepositoryImpl implements SearchRoomRepositoryCustom {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<RoomEntity> searchAvailableRooms(
//            String locationName,
//            long minPrice,
//            long maxPrice,
//            LocalDate checkIn,
//            LocalDate checkOut
//    ) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<RoomEntity> cq = cb.createQuery(RoomEntity.class);
//        Root<RoomEntity> roomRoot = cq.from(RoomEntity.class);
//
//        // Join với bảng BookingRoomEntity
//        Join<RoomEntity, BookingRoomEntity> bookingRoomJoin = roomRoot.join("bookingRooms", JoinType.LEFT);
//
//        // Các điều kiện lọc
//        Predicate locationPredicate = cb.equal(roomRoot.get("location").get("locationName"), locationName);
//        Predicate pricePredicate = cb.between(roomRoot.get("roomPrice"), minPrice, maxPrice);
//
//        // Điều kiện ngày check-in/check-out không trùng với bất kỳ booking nào
//        // Kiểm tra sự không trùng lặp giữa các booking đã có và khoảng thời gian cần tìm
//        Subquery<Long> subquery = cq.subquery(Long.class);
//        Root<BookingRoomEntity> bookingRoot = subquery.from(BookingRoomEntity.class);
//        subquery.select(cb.literal(1L))
//                .where(
//                        cb.equal(bookingRoot.get("room"), roomRoot),
//                        // Điều kiện ngày booking không trùng với khoảng thời gian yêu cầu
//                        cb.or(
//                                // Booking có thể bắt đầu trước check-out và kết thúc sau check-in
//                                cb.and(
//                                        cb.lessThanOrEqualTo(bookingRoot.get("checkOut"), checkIn),
//                                        cb.greaterThanOrEqualTo(bookingRoot.get("checkIn"), checkOut)
//                                ),
//                                // Hoặc booking bắt đầu sau check-in và kết thúc trước check-out
//                                cb.and(
//                                        cb.greaterThanOrEqualTo(bookingRoot.get("checkIn"), checkOut),
//                                        cb.lessThanOrEqualTo(bookingRoot.get("checkOut"), checkIn)
//                                )
//                        )
//                );
//
//        // Đảm bảo rằng không có booking nào tồn tại trong khoảng thời gian checkIn - checkOut
//        Predicate datePredicate = cb.not(cb.exists(subquery));
//
//        // Kết hợp các điều kiện và áp dụng vào CriteriaQuery
//        cq.where(cb.and(locationPredicate, pricePredicate, datePredicate));
//
//        // Thực thi truy vấn và trả về kết quả
//        return entityManager.createQuery(cq).getResultList();
//    }
//}



package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.entity.BookingRoomEntity;
import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.service.SearchRoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SearchRoomRepositoryImpl implements SearchRoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RoomEntity> searchAvailableRooms(
            String locationName,
            long minPrice,
            long maxPrice,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        String jpql = """
            SELECT r
            FROM RoomEntity r
            LEFT JOIN r.bookingRooms br
            WHERE r.location.locationName = :locationName
              AND r.roomPrice BETWEEN :minPrice AND :maxPrice
              AND (
                  br IS NULL OR 
                  NOT (br.checkIn < :checkOut AND br.checkOut > :checkIn)
             )
        """;



        return entityManager.createQuery(jpql, RoomEntity.class)
                .setParameter("locationName", locationName)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .setParameter("checkIn", checkIn)
                .setParameter("checkOut", checkOut)
                .getResultList();
    }
}
