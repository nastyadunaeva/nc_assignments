package com.example.jpa_h2.repository;

import java.util.List;

import com.example.jpa_h2.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonCrudRepository extends CrudRepository<Person, Long>{
	public List<Person> findByLastNameLike(String name);
	public List<Person> findByFirstNameLike(String name);
	public List<Person> findByIdLike(Long index);
}
