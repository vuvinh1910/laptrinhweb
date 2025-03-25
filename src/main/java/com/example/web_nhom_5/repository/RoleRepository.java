package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByName(String name);
}
