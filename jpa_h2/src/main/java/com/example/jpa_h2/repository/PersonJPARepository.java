package com.example.jpa_h2.repository;

import com.example.jpa_h2.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonJPARepository extends JpaRepository<Person, Long> {
    public List<Person> findByLastNameLike(String name);
    public List<Person> findByFirstNameLike(String name);
    public List<Person> findByIdLike(Long index);
    public Optional<Person> findByUsername(String username);
}
