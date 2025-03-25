package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.ForgotPassword;
import com.example.web_nhom_5.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.userEntity = ?2")
    Optional<ForgotPassword> findByOtpAndUserEntity(Integer otp, UserEntity userEntity);

    @Query("select fp from ForgotPassword fp where fp.userEntity = ?1 and fp.isVerified = ?2")
    Optional<ForgotPassword> findByUserEntityAndVerified(UserEntity userEntity, Boolean verified);

    List<ForgotPassword> findAllByUserEntity(UserEntity userEntity);
}
