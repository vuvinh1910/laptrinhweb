package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.LocationEntity;
import com.example.web_nhom_5.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    VoucherEntity findByCode(String code);
}
