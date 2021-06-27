package com.example.jpa_h2.controller;

import com.example.jpa_h2.repository.PersonCrudRepository;
import com.example.jpa_h2.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class MainController {
 
    @Autowired
    private PersonCrudRepository personCrudRepository;
 
    @RequestMapping("/all")
    public String all() {
        Iterable<Person> all = personCrudRepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
    
    @RequestMapping("/{id}")
    public String index(@PathVariable long id) {
    	Long i = new Long(id);
        Iterable<Person> all = personCrudRepository.findByIdLike(i);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
    
    @RequestMapping("/name/{lastName}")
    public String lastName(@PathVariable String lastName) {
        Iterable<Person> all = personCrudRepository.findByLastNameLike(lastName);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
}
