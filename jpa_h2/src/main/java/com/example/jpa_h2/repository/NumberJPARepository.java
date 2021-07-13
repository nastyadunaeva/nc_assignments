package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.NumberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NumberJPARepository extends JpaRepository<NumberInfo, Long> {
    public List<NumberInfo> findByNumber(int number);
}
