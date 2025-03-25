package com.example.web_nhom_5.repository;

import com.example.web_nhom_5.entity.RoomEntity;
import com.example.web_nhom_5.service.SearchRoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRoomRepository extends JpaRepository<RoomEntity, Long>, SearchRoomRepositoryCustom {
    // Các phương thức khác (nếu có)
}
