package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.Person;
import com.example.jpa_h2.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockJPARepository extends JpaRepository<Stock, Long> {
    public Stock findBySymbol(String symbol);
    //public List<Stock> findTop50By
}
