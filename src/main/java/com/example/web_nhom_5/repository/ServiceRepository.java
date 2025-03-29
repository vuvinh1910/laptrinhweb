package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, String>, JpaSpecificationExecutor<ServiceEntity> {
//    Optional<ServiceEntity> findByCodeName(String codeName);
    @Query("SELECT sr FROM ServiceEntity sr WHERE CONCAT(sr.serviceName,' ',sr.serviceDetail,' ',sr.codeName,' ',sr.servicePrice,' ') LIKE %?1%" )
    List<ServiceEntity> listAllBykeyWord(String keyword);
    @Query("SELECT s FROM BookingServiceEntity s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:paid IS NULL OR s.paid = :paid)")
    List<ServiceEntity> findServicesByFilter(
            @Param("status") String status,
            @Param("paid") String paid);

    @Query("SELECT s FROM ServiceEntity s WHERE " +
            "(:minPrice IS NULL OR s.servicePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.servicePrice <= :maxPrice) AND " +
            "(:serviceType IS NULL OR s.codeName LIKE CONCAT(:serviceType, '%'))"
    )
    List<ServiceEntity> filterService(Long minPrice, Long maxPrice, String serviceType);

}
