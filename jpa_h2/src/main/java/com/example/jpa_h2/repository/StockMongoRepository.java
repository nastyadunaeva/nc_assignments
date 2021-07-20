package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StockMongoRepository extends MongoRepository<Stock, Long>{
    public List<Stock> findAll();
    public List<Stock> findBySymbolOrderByTimeDesc(String symbol);
    public Stock findTop1BySymbolOrderByTimeDesc(String symbol);
    public Optional<Stock> findTop1BySymbolAndTimeGreaterThanOrderByTimeAsc(String symbol, Long time);
}
