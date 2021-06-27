package com.example.jpa_h2.controller;

import com.example.jpa_h2.repository.PersonCrudRepository;
import com.example.jpa_h2.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class MainController {
 
    @Autowired
    private PersonCrudRepository personCrudRepository;
 
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String all() {
        Iterable<Person> all = personCrudRepository.findAll();
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
    
    @GetMapping("/{id}")
    public String index(@PathVariable long id) {
    	Long i = new Long(id);
        Iterable<Person> all = personCrudRepository.findByIdLike(i);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
    
    @GetMapping("/name/{lastName}")
    public String lastName(@PathVariable String lastName) {
        Iterable<Person> all = personCrudRepository.findByLastNameLike(lastName);
        StringBuilder sb = new StringBuilder();
        all.forEach(p -> sb.append(p.getFirstName()+ " " + p.getLastName() + "<br>"));
        return sb.toString();
    }
    
    @PostMapping("/add")
    public String add(@RequestBody Person person) {
    	return person.getFirstName() + " " + person.getLastName() + " was added to the database";
    }
}
