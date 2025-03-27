package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    @Query("select count(b) from UserEntity b")
    long countUser();
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUserName(String UserName);
}
