package com.example.web_nhom_5.service.implement;

import com.example.web_nhom_5.entity.VoucherEntity;
import com.example.web_nhom_5.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class VoucherService {
    @Autowired
    VoucherRepository voucherRepository;

    public List<VoucherEntity> getAllVoucher() {
        return voucherRepository.findAll();
    }

    public VoucherEntity getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public VoucherEntity getVoucherById(Long id) {
        return voucherRepository.findById(id).orElseThrow(()->new RuntimeException("Voucher Not Found"));
    }

    public void addVoucher(VoucherEntity voucherEntity) {
        voucherEntity.setUserVouchers(new ArrayList<>());
        voucherRepository.save(voucherEntity);
    }

    public void updateVoucher(VoucherEntity voucherEntity, Long id) {
        // 1. Lấy VoucherEntity hiện có từ database (Đây là thực thể MANAGED)
        VoucherEntity existingVoucher = getVoucherById(id);
        existingVoucher.setDescription(voucherEntity.getDescription());
        existingVoucher.setCode(voucherEntity.getCode());
        existingVoucher.setValue(voucherEntity.getValue());
        existingVoucher.setRemain(voucherEntity.getRemain());
        voucherRepository.save(existingVoucher); // Lưu thực thể đã được cập nhật
    }

    public void decreaseVoucher(Long id) {
        VoucherEntity voucher = getVoucherById(id);
        if(voucher.getRemain()<=0) {
            throw new RuntimeException("Voucher da het luot");
        } else {
            voucher.setRemain(voucher.getRemain() - 1);
            voucherRepository.save(voucher);
        }
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}
