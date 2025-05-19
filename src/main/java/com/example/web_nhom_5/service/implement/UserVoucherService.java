package com.example.web_nhom_5.service.implement;


import com.example.web_nhom_5.entity.UserEntity;
import com.example.web_nhom_5.entity.UserVoucherEntity;
import com.example.web_nhom_5.entity.VoucherEntity;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.UserRepository;
import com.example.web_nhom_5.repository.UserVoucherRepository;
import com.example.web_nhom_5.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserVoucherService {
    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private UserService userService;

    public UserVoucherEntity getUserVoucherById(Long id) {
        return userVoucherRepository.findById(id).orElseThrow(()->new RuntimeException("User Voucher Not Found"));
    }

    public Integer checkAndGetValueValidVoucherUser(String code) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        UserVoucherEntity userVoucher = userVoucherRepository.findByVoucher_CodeAndUser_Id(code,user.getId());
        if(userVoucher!=null) {
            if(!userVoucher.isUsed()) {
                return userVoucher.getVoucher().getValue();
            }
        }
        return 0;
    }

    public List<UserVoucherEntity> getAllUserVoucherByUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        return userVoucherRepository.findAllByUser_Id(user.getId());
    }

    public List<UserVoucherEntity> getAllUserVoucherValid() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        return userVoucherRepository.findAllByIsUsedAndUser_IdAndVoucher_RemainGreaterThan(false, user.getId(), 0);
    }

    public void addUserVoucher(Long voucherId) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name)
                .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));

        if (!userVoucherRepository.existsUserVoucherByUser_IdAndVoucher_Id(user.getId(), voucherId)) {
            VoucherEntity voucher = voucherService.getVoucherById(voucherId);

            if(voucher.getRemain()<=0) {
                throw new RuntimeException("Voucher đã hết.");
            } else {
                voucher.setRemain(voucher.getRemain()-1);
                voucherRepository.save(voucher);
            }

            UserVoucherEntity userVoucherEntity = new UserVoucherEntity();
            userVoucherEntity.setUser(user);
            userVoucherEntity.setVoucher(voucher);
            userVoucherEntity.setUsed(false);

            if (voucher.getUserVouchers() != null) {
                voucher.getUserVouchers().add(userVoucherEntity);
            }
            if (user.getUserVouchers() != null) {
                user.getUserVouchers().add(userVoucherEntity);
            }

            userVoucherRepository.save(userVoucherEntity);
        } else {
            throw new RuntimeException("Bạn đã claim Voucher rổi");
        }
    }

    public void updateUserVoucher(Long id, boolean used) {
        UserVoucherEntity userVoucherEntity = getUserVoucherById(id);
        userVoucherEntity.setUsed(used);
        userVoucherRepository.save(userVoucherEntity);
    }

    public void deleteUserVoucher(Long id) {
        userVoucherRepository.deleteById(id);
    }

}
