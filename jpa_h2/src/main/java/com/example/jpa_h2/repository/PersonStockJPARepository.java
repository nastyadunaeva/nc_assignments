package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.PersonStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonStockJPARepository extends JpaRepository<PersonStock, Long> {
    public Optional<PersonStock> findByPersonIdAndStockId(Long personId, Long stockId);
    public List<PersonStock> findByPersonId(Long personId);
}
