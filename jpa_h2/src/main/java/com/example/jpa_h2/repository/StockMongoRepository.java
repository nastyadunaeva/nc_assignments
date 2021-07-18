package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockMongoRepository extends MongoRepository<Stock, Long>{
    public List<Stock> findAll();
    public Stock findBySymbol(String symbol);
}
