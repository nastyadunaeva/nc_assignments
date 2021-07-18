package com.example.jpa_h2.repository;
import com.example.jpa_h2.entity.Cat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CatsRepository extends MongoRepository<Cat, Long>{
    List<Cat> findAll();
}
