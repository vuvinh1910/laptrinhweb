package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.UserVoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVoucherRepository extends JpaRepository<UserVoucherEntity,Long> {
    boolean existsUserVoucherByUser_IdAndVoucher_Id(Long userId, Long voucherId);
    List<UserVoucherEntity> findAllByIsUsedAndUser_IdAndVoucher_RemainGreaterThan(Boolean used, Long userId, int voucherRemain);
    List<UserVoucherEntity> findAllByUser_Id(Long userId);
    UserVoucherEntity findByVoucher_CodeAndUser_Id(String code, Long userId);
}
